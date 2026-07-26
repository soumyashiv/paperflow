package com.paperflow.app.presentation.aichat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.datastore.PreferencesDataStore
import com.paperflow.app.domain.model.ChatMessage
import com.paperflow.app.domain.repository.PageRepository
import com.paperflow.app.domain.repository.SearchRepository
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class AIChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isThinking: Boolean = false,
    val contextDocumentName: String? = null,
    val hasApiKey: Boolean = false,
    val mode: AIMode = AIMode.OFFLINE,
)

enum class AIMode { OFFLINE, ONLINE }

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val searchRepo: SearchRepository,
    private val pageRepo: PageRepository,
    private val getDocument: GetDocumentByIdUseCase,
    private val prefs: PreferencesDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(AIChatUiState())
    val state: StateFlow<AIChatUiState> = _state.asStateFlow()

    fun init(documentId: Long?) {
        viewModelScope.launch {
            val hasKey = !prefs.geminiApiKey.first().isNullOrBlank()
            val docName = documentId?.let { getDocument(it)?.name }
            _state.update {
                it.copy(
                    hasApiKey = hasKey,
                    contextDocumentName = docName,
                    mode = if (hasKey) AIMode.ONLINE else AIMode.OFFLINE,
                )
            }
            // Welcome message
            addBotMessage(
                if (docName != null) "Hi! I'm ready to answer questions about \"$docName\". What would you like to know?"
                else "Hi! I can answer questions about your documents. Ask me anything!",
            )
        }
    }

    fun setInput(text: String) = _state.update { it.copy(inputText = text) }

    fun sendMessage() {
        val query = _state.value.inputText.trim()
        if (query.isBlank() || _state.value.isThinking) return
        _state.update { it.copy(inputText = "", isThinking = true, messages = it.messages + ChatMessage(text = query, isUser = true)) }

        viewModelScope.launch {
            val response = generateResponse(query)
            addBotMessage(response.first, response.second)
            _state.update { it.copy(isThinking = false) }
        }
    }

    /**
     * Offline AI: FTS + OCR text retrieval → synthesised answer.
     * Online AI: Gemini API (if key configured).
     */
    private suspend fun generateResponse(query: String): Pair<String, List<String>> = withContext(Dispatchers.IO) {
        if (_state.value.mode == AIMode.ONLINE && _state.value.hasApiKey) {
            return@withContext generateOnlineResponse(query)
        }
        // Offline: search OCR index
        val results = searchRepo.searchOcr(query)
        if (results.isEmpty()) {
            return@withContext Pair(
                "I couldn't find relevant information in your documents. Try scanning documents with better quality or check if OCR has completed.",
                emptyList(),
            )
        }
        val sources = results.take(3).map { it.documentName }.distinct()
        val context = results.take(3).joinToString("\n\n") { it.matchSnippet }
        val answer = buildOfflineAnswer(query, context)
        Pair(answer, sources)
    }

    private fun buildOfflineAnswer(query: String, context: String): String {
        // Simple keyword-extraction response — no LLM, pure offline
        return "Based on your documents, here's what I found:\n\n$context\n\n*Source: matched from your scanned documents using OCR text search.*"
    }

    private suspend fun generateOnlineResponse(query: String): Pair<String, List<String>> {
        // Gemini API integration — placeholder, key retrieved from prefs
        // Actual HTTP call would use Retrofit with the key from prefs
        return Pair("Online AI response would appear here with your Gemini API key.", emptyList())
    }

    private fun addBotMessage(text: String, sources: List<String> = emptyList()) {
        _state.update { it.copy(messages = it.messages + ChatMessage(text = text, isUser = false, sourceDocuments = sources)) }
    }

    fun clearConversation() = _state.update { it.copy(messages = emptyList()) }
}
