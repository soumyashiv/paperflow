package com.paperflow.app.domain.model

// ─── Document ─────────────────────────────────────────────────────────────────
data class Document(
    val id: Long = 0,
    val name: String,
    val type: DocumentType,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailPath: String? = null,
    val folderId: Long? = null,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false,
    val filePath: String,
    val pageCount: Int = 1,
    val sizeBytes: Long = 0,
    val isFavorite: Boolean = false,
    val ocrStatus: OcrStatus = OcrStatus.PENDING,
    val lastReadPage: Int = 0,
)

enum class DocumentType(val ext: String) {
    PDF("pdf"), JPG("jpg"), PNG("png"), NOTE("note");
    companion object {
        fun from(s: String): DocumentType = entries.firstOrNull { it.ext == s.lowercase() } ?: PDF
    }
}

enum class OcrStatus { PENDING, PROCESSING, COMPLETE, FAILED }

// ─── Page ─────────────────────────────────────────────────────────────────────
data class Page(
    val id: Long = 0,
    val documentId: Long,
    val pageNumber: Int,
    val thumbnailPath: String? = null,
    val previewPath: String? = null,
    val extractedText: String? = null,
    val orderIndex: Int = 0,
)

// ─── Note ─────────────────────────────────────────────────────────────────────
data class Note(
    val id: Long = 0,
    val title: String,
    val content: String = "",
    val linkedDocumentId: Long? = null,
    val folderId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isHandwritten: Boolean = false,
    val isFavorite: Boolean = false,
    val thumbnailPath: String? = null,
)

// ─── Annotation ───────────────────────────────────────────────────────────────
data class Annotation(
    val id: Long = 0,
    val pageId: Long,
    val type: AnnotationType,
    val positionData: String, // JSON
    val color: String = "#FFFF00",
    val content: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

enum class AnnotationType {
    HIGHLIGHT, UNDERLINE, STRIKETHROUGH, TEXT, IMAGE, FREEHAND, SHAPE;
    companion object {
        fun from(s: String): AnnotationType =
            entries.firstOrNull { it.name.equals(s, ignoreCase = true) } ?: HIGHLIGHT
    }
}

// ─── Folder ───────────────────────────────────────────────────────────────────
data class Folder(
    val id: Long = 0,
    val name: String,
    val parentFolderId: Long? = null,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false,
    val colorHex: String = "#F5C842",
    val iconName: String = "folder",
    val createdAt: Long = System.currentTimeMillis(),
    val itemCount: Int = 0, // computed, not stored
)

// ─── Tag ──────────────────────────────────────────────────────────────────────
data class Tag(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#2196F3",
)

// ─── Activity ─────────────────────────────────────────────────────────────────
data class ActivityItem(
    val id: Long = 0,
    val documentId: Long,
    val action: String,
    val timestamp: Long = System.currentTimeMillis(),
)

// ─── Search Results ───────────────────────────────────────────────────────────
data class SearchResult(
    val documentId: Long,
    val documentName: String,
    val thumbnailPath: String?,
    val pageId: Long?,
    val pageNumber: Int?,
    val matchSnippet: String,
    val resultType: SearchResultType,
)

enum class SearchResultType { DOCUMENT, PAGE_TEXT, NOTE, FOLDER, ANNOTATION }

// ─── Scan Session ─────────────────────────────────────────────────────────────
data class ScanSession(
    val id: String,
    val capturedImagePaths: List<String> = emptyList(),
    val filter: ScanFilter = ScanFilter.COLOR,
)

enum class ScanFilter { COLOR, GRAYSCALE, BLACK_WHITE, ORIGINAL }

// ─── Storage Info ─────────────────────────────────────────────────────────────
data class StorageInfo(
    val usedBytes: Long,
    val totalBytes: Long,
) {
    val usedPercent: Float get() = if (totalBytes > 0) usedBytes.toFloat() / totalBytes else 0f
}

// ─── AI Chat ──────────────────────────────────────────────────────────────────
data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceDocuments: List<String> = emptyList(), // document names that provided context
)
