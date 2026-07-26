package com.paperflow.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.datastore.PreferencesDataStore
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.domain.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "",
    val appTheme: String = "LIGHT",
    val biometricEnabled: Boolean = true,
    val screenshotProtection: Boolean = true,
    val scannerAutoOcr: Boolean = true,
    val scannerAutoCrop: Boolean = true,
    val scannerDefaultFilter: String = "color",
    val pdfDefaultView: String = "single",
    val pdfPageSound: Boolean = false,
    val vaultTimeoutMinutes: Int = 10,
    val geminiApiKey: String = "",
    val notesFontSize: Int = 16,
    val documentCount: Int = 0,
    val storageUsed: String = "0 MB",
    val storagePercent: Float = 0f,
    val showNameEdit: Boolean = false,
    
    val scannerAutoCapture: Boolean = false,
    val scannerPerspectiveCorrection: Boolean = true,
    val scannerSmartRotation: Boolean = true,
    val scannerMultiPage: Boolean = false,
    val scannerDefaultQuality: String = "high",
    val scannerImageEnhancement: Boolean = true,
    val scannerDefaultOutput: String = "pdf",
    val scannerHapticFeedback: Boolean = true,
    val scannerCameraGrid: Boolean = false,
    val scannerFlashPref: String = "auto",
    val scannerSaveOriginal: Boolean = false,
    val scannerAutoSave: Boolean = false,
    val pdfPageFlipAnim: String = "slide",
    val pdfRememberLastPage: Boolean = true,
    val pdfDefaultZoom: String = "fit_width",
    val pdfReadingDirection: String = "vertical",
    val pdfNightMode: Boolean = false,
    val pdfAmoledMode: Boolean = false,
    val pdfBrightnessOverride: Boolean = false,
    val pdfKeepScreenAwake: Boolean = true,
    val ocrEnabled: Boolean = true,
    val ocrOfflineMode: Boolean = true,
    val ocrLanguages: Set<String> = setOf("en"),
    val ocrHandwriting: Boolean = false,
    val ocrAutoIndex: Boolean = true,
    val ocrAccuracyMode: String = "balanced",
    val ocrBackgroundProc: Boolean = true,
    val notesDefaultFont: String = "inter",
    val notesRichText: Boolean = true,
    val notesAutoSave: Boolean = true,
    val notesConvertScan: Boolean = false,
    val notesMarkdown: Boolean = false,
    val notesSpellCheck: Boolean = true,
    val notesLineNumbers: Boolean = false,
    val searchOcrText: Boolean = true,
    val searchNotes: Boolean = true,
    val searchAnnotations: Boolean = true,
    val searchFileNames: Boolean = true,
    val searchFolderNames: Boolean = true,
    val searchInstant: Boolean = true,
    val searchHighlight: Boolean = true,
    val searchHistoryEnabled: Boolean = true,
    val workspaceGroupBy: String = "none",
    val workspaceShowRecent: Boolean = true,
    val workspaceShowHidden: Boolean = false,
    val workspaceTimelineView: Boolean = false,
    val workspaceDefaultFolder: Long = -1L,
    val securityAppLock: Boolean = false,
    val securityLockHiddenFolders: Boolean = true,
    val securityHideSensitive: Boolean = false,
    val themeAccentColor: String = "amber",
    val themeDynamicColors: Boolean = false,
    val themeFontSize: String = "medium",
    val themeAppLanguage: String = "system",
    val themeAnimations: Boolean = true,
    val themeReduceMotion: Boolean = false,
    val backupDocuments: Boolean = true,
    val backupNotes: Boolean = true,
    val backupSettings: Boolean = true,
    val notifScanComplete: Boolean = true,
    val notifOcrComplete: Boolean = true,
    val notifBackgroundTasks: Boolean = true,
    val notifReminders: Boolean = true,
    val notifUpdates: Boolean = true,
    val accessibilityLargerText: Boolean = false,
    val accessibilityHighContrast: Boolean = false,
    val accessibilityScreenReader: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: PreferencesDataStore,
    private val documentRepo: DocumentRepository,
    private val storage: FileStorage,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val flowList = listOf(
                prefs.userName,
                prefs.appTheme,
                prefs.biometricEnabled,
                prefs.screenshotProtection,
                prefs.scannerAutoOcr,
                prefs.scannerAutoCrop,
                prefs.scannerDefaultFilter,
                prefs.pdfDefaultView,
                prefs.pdfPageSound,
                prefs.vaultTimeoutMinutes,
                prefs.geminiApiKey,
                prefs.notesFontSize,
                prefs.isFirstLaunch, // Dummy or skip
                    prefs.scannerAutoCapture,
                    prefs.scannerPerspectiveCorrection,
                    prefs.scannerSmartRotation,
                    prefs.scannerMultiPage,
                    prefs.scannerDefaultQuality,
                    prefs.scannerImageEnhancement,
                    prefs.scannerDefaultOutput,
                    prefs.scannerHapticFeedback,
                    prefs.scannerCameraGrid,
                    prefs.scannerFlashPref,
                    prefs.scannerSaveOriginal,
                    prefs.scannerAutoSave,
                    prefs.pdfPageFlipAnim,
                    prefs.pdfRememberLastPage,
                    prefs.pdfDefaultZoom,
                    prefs.pdfReadingDirection,
                    prefs.pdfNightMode,
                    prefs.pdfAmoledMode,
                    prefs.pdfBrightnessOverride,
                    prefs.pdfKeepScreenAwake,
                    prefs.ocrEnabled,
                    prefs.ocrOfflineMode,
                    prefs.ocrLanguages,
                    prefs.ocrHandwriting,
                    prefs.ocrAutoIndex,
                    prefs.ocrAccuracyMode,
                    prefs.ocrBackgroundProc,
                    prefs.notesDefaultFont,
                    prefs.notesRichText,
                    prefs.notesAutoSave,
                    prefs.notesConvertScan,
                    prefs.notesMarkdown,
                    prefs.notesSpellCheck,
                    prefs.notesLineNumbers,
                    prefs.searchOcrText,
                    prefs.searchNotes,
                    prefs.searchAnnotations,
                    prefs.searchFileNames,
                    prefs.searchFolderNames,
                    prefs.searchInstant,
                    prefs.searchHighlight,
                    prefs.searchHistoryEnabled,
                    prefs.workspaceGroupBy,
                    prefs.workspaceShowRecent,
                    prefs.workspaceShowHidden,
                    prefs.workspaceTimelineView,
                    prefs.workspaceDefaultFolder,
                    prefs.securityAppLock,
                    prefs.securityLockHiddenFolders,
                    prefs.securityHideSensitive,
                    prefs.themeAccentColor,
                    prefs.themeDynamicColors,
                    prefs.themeFontSize,
                    prefs.themeAppLanguage,
                    prefs.themeAnimations,
                    prefs.themeReduceMotion,
                    prefs.backupDocuments,
                    prefs.backupNotes,
                    prefs.backupSettings,
                    prefs.notifScanComplete,
                    prefs.notifOcrComplete,
                    prefs.notifBackgroundTasks,
                    prefs.notifReminders,
                    prefs.notifUpdates,
                    prefs.accessibilityLargerText,
                    prefs.accessibilityHighContrast,
                    prefs.accessibilityScreenReader,
            )

            combine(flowList) { args ->
                SettingsUiState(
                    userName = args[0] as String,
                    appTheme = args[1] as String,
                    biometricEnabled = args[2] as Boolean,
                    screenshotProtection = args[3] as Boolean,
                    scannerAutoOcr = args[4] as Boolean,
                    scannerAutoCrop = args[5] as Boolean,
                    scannerDefaultFilter = args[6] as String,
                    pdfDefaultView = args[7] as String,
                    pdfPageSound = args[8] as Boolean,
                    vaultTimeoutMinutes = args[9] as Int,
                    geminiApiKey = args[10] as? String ?: "",
                    notesFontSize = args[11] as Int,
                    // 12 is isFirstLaunch
                        scannerAutoCapture = args[13] as Boolean,
                        scannerPerspectiveCorrection = args[14] as Boolean,
                        scannerSmartRotation = args[15] as Boolean,
                        scannerMultiPage = args[16] as Boolean,
                        scannerDefaultQuality = args[17] as String,
                        scannerImageEnhancement = args[18] as Boolean,
                        scannerDefaultOutput = args[19] as String,
                        scannerHapticFeedback = args[20] as Boolean,
                        scannerCameraGrid = args[21] as Boolean,
                        scannerFlashPref = args[22] as String,
                        scannerSaveOriginal = args[23] as Boolean,
                        scannerAutoSave = args[24] as Boolean,
                        pdfPageFlipAnim = args[25] as String,
                        pdfRememberLastPage = args[26] as Boolean,
                        pdfDefaultZoom = args[27] as String,
                        pdfReadingDirection = args[28] as String,
                        pdfNightMode = args[29] as Boolean,
                        pdfAmoledMode = args[30] as Boolean,
                        pdfBrightnessOverride = args[31] as Boolean,
                        pdfKeepScreenAwake = args[32] as Boolean,
                        ocrEnabled = args[33] as Boolean,
                        ocrOfflineMode = args[34] as Boolean,
                        ocrLanguages = args[35] as @Suppress("UNCHECKED_CAST") Set<String>,
                        ocrHandwriting = args[36] as Boolean,
                        ocrAutoIndex = args[37] as Boolean,
                        ocrAccuracyMode = args[38] as String,
                        ocrBackgroundProc = args[39] as Boolean,
                        notesDefaultFont = args[40] as String,
                        notesRichText = args[41] as Boolean,
                        notesAutoSave = args[42] as Boolean,
                        notesConvertScan = args[43] as Boolean,
                        notesMarkdown = args[44] as Boolean,
                        notesSpellCheck = args[45] as Boolean,
                        notesLineNumbers = args[46] as Boolean,
                        searchOcrText = args[47] as Boolean,
                        searchNotes = args[48] as Boolean,
                        searchAnnotations = args[49] as Boolean,
                        searchFileNames = args[50] as Boolean,
                        searchFolderNames = args[51] as Boolean,
                        searchInstant = args[52] as Boolean,
                        searchHighlight = args[53] as Boolean,
                        searchHistoryEnabled = args[54] as Boolean,
                        workspaceGroupBy = args[55] as String,
                        workspaceShowRecent = args[56] as Boolean,
                        workspaceShowHidden = args[57] as Boolean,
                        workspaceTimelineView = args[58] as Boolean,
                        workspaceDefaultFolder = args[59] as Long,
                        securityAppLock = args[60] as Boolean,
                        securityLockHiddenFolders = args[61] as Boolean,
                        securityHideSensitive = args[62] as Boolean,
                        themeAccentColor = args[63] as String,
                        themeDynamicColors = args[64] as Boolean,
                        themeFontSize = args[65] as String,
                        themeAppLanguage = args[66] as String,
                        themeAnimations = args[67] as Boolean,
                        themeReduceMotion = args[68] as Boolean,
                        backupDocuments = args[69] as Boolean,
                        backupNotes = args[70] as Boolean,
                        backupSettings = args[71] as Boolean,
                        notifScanComplete = args[72] as Boolean,
                        notifOcrComplete = args[73] as Boolean,
                        notifBackgroundTasks = args[74] as Boolean,
                        notifReminders = args[75] as Boolean,
                        notifUpdates = args[76] as Boolean,
                        accessibilityLargerText = args[77] as Boolean,
                        accessibilityHighContrast = args[78] as Boolean,
                        accessibilityScreenReader = args[79] as Boolean,
                )
            }.collect { combinedState ->
                _state.update { 
                    combinedState.copy(
                        documentCount = it.documentCount,
                        storageUsed = it.storageUsed,
                        storagePercent = it.storagePercent,
                        showNameEdit = it.showNameEdit
                    )
                }
            }
        }
        loadStorageInfo()
    }

    private fun loadStorageInfo() {
        viewModelScope.launch {
            val bytes = documentRepo.getTotalStorageBytes()
            _state.update { state ->
                state.copy(
                    storageUsed = storage.formatSize(bytes),
                    storagePercent = (bytes.toFloat() / storage.documentsDir.totalSpace.coerceAtLeast(1L)).coerceIn(0f, 1f),
                )
            }
        }
    }

    fun setTheme(theme: String) = viewModelScope.launch { prefs.setAppTheme(theme) }
    fun setBiometric(enabled: Boolean) = viewModelScope.launch { prefs.setBiometricEnabled(enabled) }
    fun setScreenshotProtection(enabled: Boolean) = viewModelScope.launch { prefs.setScreenshotProtection(enabled) }
    fun setScannerAutoOcr(enabled: Boolean) = viewModelScope.launch { prefs.setScannerAutoOcr(enabled) }
    fun setScannerAutoCrop(enabled: Boolean) = viewModelScope.launch { prefs.setScannerAutoCrop(enabled) }
    fun setScannerFilter(filter: String) = viewModelScope.launch { prefs.setScannerFilter(filter) }
    fun setPdfView(view: String) = viewModelScope.launch { prefs.setPdfDefaultView(view) }
    fun setPdfPageSound(enabled: Boolean) = viewModelScope.launch { prefs.setPdfPageSound(enabled) }
    fun setVaultTimeout(minutes: Int) = viewModelScope.launch { prefs.setVaultTimeout(minutes) }
    fun setGeminiKey(key: String) = viewModelScope.launch { prefs.setGeminiApiKey(key.ifBlank { null }) }
    fun setNotesFontSize(size: Int) = viewModelScope.launch { prefs.setNotesFontSize(size) }
    fun saveName(name: String) = viewModelScope.launch {
        prefs.setUserName(name)
        _state.update { it.copy(showNameEdit = false) }
    }
    fun promptNameEdit() = _state.update { it.copy(showNameEdit = true) }
    fun dismissNameEdit() = _state.update { it.copy(showNameEdit = false) }

    fun setScannerAutoCapture(value: Boolean) = viewModelScope.launch { prefs.setScannerAutoCapture(value) }
    fun setScannerPerspectiveCorrection(value: Boolean) = viewModelScope.launch { prefs.setScannerPerspectiveCorrection(value) }
    fun setScannerSmartRotation(value: Boolean) = viewModelScope.launch { prefs.setScannerSmartRotation(value) }
    fun setScannerMultiPage(value: Boolean) = viewModelScope.launch { prefs.setScannerMultiPage(value) }
    fun setScannerDefaultQuality(value: String) = viewModelScope.launch { prefs.setScannerDefaultQuality(value) }
    fun setScannerImageEnhancement(value: Boolean) = viewModelScope.launch { prefs.setScannerImageEnhancement(value) }
    fun setScannerDefaultOutput(value: String) = viewModelScope.launch { prefs.setScannerDefaultOutput(value) }
    fun setScannerHapticFeedback(value: Boolean) = viewModelScope.launch { prefs.setScannerHapticFeedback(value) }
    fun setScannerCameraGrid(value: Boolean) = viewModelScope.launch { prefs.setScannerCameraGrid(value) }
    fun setScannerFlashPref(value: String) = viewModelScope.launch { prefs.setScannerFlashPref(value) }
    fun setScannerSaveOriginal(value: Boolean) = viewModelScope.launch { prefs.setScannerSaveOriginal(value) }
    fun setScannerAutoSave(value: Boolean) = viewModelScope.launch { prefs.setScannerAutoSave(value) }
    fun setPdfPageFlipAnim(value: String) = viewModelScope.launch { prefs.setPdfPageFlipAnim(value) }
    fun setPdfRememberLastPage(value: Boolean) = viewModelScope.launch { prefs.setPdfRememberLastPage(value) }
    fun setPdfDefaultZoom(value: String) = viewModelScope.launch { prefs.setPdfDefaultZoom(value) }
    fun setPdfReadingDirection(value: String) = viewModelScope.launch { prefs.setPdfReadingDirection(value) }
    fun setPdfNightMode(value: Boolean) = viewModelScope.launch { prefs.setPdfNightMode(value) }
    fun setPdfAmoledMode(value: Boolean) = viewModelScope.launch { prefs.setPdfAmoledMode(value) }
    fun setPdfBrightnessOverride(value: Boolean) = viewModelScope.launch { prefs.setPdfBrightnessOverride(value) }
    fun setPdfKeepScreenAwake(value: Boolean) = viewModelScope.launch { prefs.setPdfKeepScreenAwake(value) }
    fun setOcrEnabled(value: Boolean) = viewModelScope.launch { prefs.setOcrEnabled(value) }
    fun setOcrOfflineMode(value: Boolean) = viewModelScope.launch { prefs.setOcrOfflineMode(value) }
    fun setOcrLanguages(value: Set<String>) = viewModelScope.launch { prefs.setOcrLanguages(value) }
    fun setOcrHandwriting(value: Boolean) = viewModelScope.launch { prefs.setOcrHandwriting(value) }
    fun setOcrAutoIndex(value: Boolean) = viewModelScope.launch { prefs.setOcrAutoIndex(value) }
    fun setOcrAccuracyMode(value: String) = viewModelScope.launch { prefs.setOcrAccuracyMode(value) }
    fun setOcrBackgroundProc(value: Boolean) = viewModelScope.launch { prefs.setOcrBackgroundProc(value) }
    fun setNotesDefaultFont(value: String) = viewModelScope.launch { prefs.setNotesDefaultFont(value) }
    fun setNotesRichText(value: Boolean) = viewModelScope.launch { prefs.setNotesRichText(value) }
    fun setNotesAutoSave(value: Boolean) = viewModelScope.launch { prefs.setNotesAutoSave(value) }
    fun setNotesConvertScan(value: Boolean) = viewModelScope.launch { prefs.setNotesConvertScan(value) }
    fun setNotesMarkdown(value: Boolean) = viewModelScope.launch { prefs.setNotesMarkdown(value) }
    fun setNotesSpellCheck(value: Boolean) = viewModelScope.launch { prefs.setNotesSpellCheck(value) }
    fun setNotesLineNumbers(value: Boolean) = viewModelScope.launch { prefs.setNotesLineNumbers(value) }
    fun setSearchOcrText(value: Boolean) = viewModelScope.launch { prefs.setSearchOcrText(value) }
    fun setSearchNotes(value: Boolean) = viewModelScope.launch { prefs.setSearchNotes(value) }
    fun setSearchAnnotations(value: Boolean) = viewModelScope.launch { prefs.setSearchAnnotations(value) }
    fun setSearchFileNames(value: Boolean) = viewModelScope.launch { prefs.setSearchFileNames(value) }
    fun setSearchFolderNames(value: Boolean) = viewModelScope.launch { prefs.setSearchFolderNames(value) }
    fun setSearchInstant(value: Boolean) = viewModelScope.launch { prefs.setSearchInstant(value) }
    fun setSearchHighlight(value: Boolean) = viewModelScope.launch { prefs.setSearchHighlight(value) }
    fun setSearchHistoryEnabled(value: Boolean) = viewModelScope.launch { prefs.setSearchHistoryEnabled(value) }
    fun setWorkspaceGroupBy(value: String) = viewModelScope.launch { prefs.setWorkspaceGroupBy(value) }
    fun setWorkspaceShowRecent(value: Boolean) = viewModelScope.launch { prefs.setWorkspaceShowRecent(value) }
    fun setWorkspaceShowHidden(value: Boolean) = viewModelScope.launch { prefs.setWorkspaceShowHidden(value) }
    fun setWorkspaceTimelineView(value: Boolean) = viewModelScope.launch { prefs.setWorkspaceTimelineView(value) }
    fun setWorkspaceDefaultFolder(value: Long) = viewModelScope.launch { prefs.setWorkspaceDefaultFolder(value) }
    fun setSecurityAppLock(value: Boolean) = viewModelScope.launch { prefs.setSecurityAppLock(value) }
    fun setSecurityLockHiddenFolders(value: Boolean) = viewModelScope.launch { prefs.setSecurityLockHiddenFolders(value) }
    fun setSecurityHideSensitive(value: Boolean) = viewModelScope.launch { prefs.setSecurityHideSensitive(value) }
    fun setThemeAccentColor(value: String) = viewModelScope.launch { prefs.setThemeAccentColor(value) }
    fun setThemeDynamicColors(value: Boolean) = viewModelScope.launch { prefs.setThemeDynamicColors(value) }
    fun setThemeFontSize(value: String) = viewModelScope.launch { prefs.setThemeFontSize(value) }
    fun setThemeAppLanguage(value: String) = viewModelScope.launch { prefs.setThemeAppLanguage(value) }
    fun setThemeAnimations(value: Boolean) = viewModelScope.launch { prefs.setThemeAnimations(value) }
    fun setThemeReduceMotion(value: Boolean) = viewModelScope.launch { prefs.setThemeReduceMotion(value) }
    fun setBackupDocuments(value: Boolean) = viewModelScope.launch { prefs.setBackupDocuments(value) }
    fun setBackupNotes(value: Boolean) = viewModelScope.launch { prefs.setBackupNotes(value) }
    fun setBackupSettings(value: Boolean) = viewModelScope.launch { prefs.setBackupSettings(value) }
    fun setNotifScanComplete(value: Boolean) = viewModelScope.launch { prefs.setNotifScanComplete(value) }
    fun setNotifOcrComplete(value: Boolean) = viewModelScope.launch { prefs.setNotifOcrComplete(value) }
    fun setNotifBackgroundTasks(value: Boolean) = viewModelScope.launch { prefs.setNotifBackgroundTasks(value) }
    fun setNotifReminders(value: Boolean) = viewModelScope.launch { prefs.setNotifReminders(value) }
    fun setNotifUpdates(value: Boolean) = viewModelScope.launch { prefs.setNotifUpdates(value) }
    fun setAccessibilityLargerText(value: Boolean) = viewModelScope.launch { prefs.setAccessibilityLargerText(value) }
    fun setAccessibilityHighContrast(value: Boolean) = viewModelScope.launch { prefs.setAccessibilityHighContrast(value) }
    fun setAccessibilityScreenReader(value: Boolean) = viewModelScope.launch { prefs.setAccessibilityScreenReader(value) }
}
