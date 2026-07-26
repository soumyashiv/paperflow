package com.paperflow.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "paperflow_prefs")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.dataStore

    // ── Keys ──────────────────────────────────────────────────────────────────
    private object Keys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val APP_THEME = stringPreferencesKey("app_theme")            // LIGHT / DARK / AMOLED
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AVATAR_PATH = stringPreferencesKey("user_avatar_path")
        val SCANNER_DEFAULT_FILTER = stringPreferencesKey("scanner_filter")   // color/grayscale/bw
        val SCANNER_AUTO_CROP = booleanPreferencesKey("scanner_auto_crop")
        val SCANNER_AUTO_OCR = booleanPreferencesKey("scanner_auto_ocr")
        val PDF_DEFAULT_VIEW = stringPreferencesKey("pdf_default_view")       // single/double/scroll
        val PDF_PAGE_SOUND = booleanPreferencesKey("pdf_page_sound")
        val VAULT_TIMEOUT_MINUTES = intPreferencesKey("vault_timeout")        // 5,10,15,30
        val VAULT_LOCK_ON_BACKGROUND = booleanPreferencesKey("vault_lock_bg")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val SCREENSHOT_PROTECTION = booleanPreferencesKey("screenshot_protection")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")           // Optional AI key
        val CLIPBOARD_AUTO_CLEAR = booleanPreferencesKey("clipboard_auto_clear")
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val VIEW_MODE = stringPreferencesKey("view_mode")                     // list/grid/large
        val NOTES_FONT_SIZE = intPreferencesKey("notes_font_size")

        // New Keys
        val SCANNER_AUTO_CAPTURE = booleanPreferencesKey("scanner_auto_capture")
        val SCANNER_PERSPECTIVE_CORRECTION = booleanPreferencesKey("scanner_perspective_correction")
        val SCANNER_SMART_ROTATION = booleanPreferencesKey("scanner_smart_rotation")
        val SCANNER_MULTI_PAGE = booleanPreferencesKey("scanner_multi_page")
        val SCANNER_DEFAULT_QUALITY = stringPreferencesKey("scanner_default_quality")
        val SCANNER_IMAGE_ENHANCEMENT = booleanPreferencesKey("scanner_image_enhancement")
        val SCANNER_DEFAULT_OUTPUT = stringPreferencesKey("scanner_default_output")
        val SCANNER_HAPTIC_FEEDBACK = booleanPreferencesKey("scanner_haptic_feedback")
        val SCANNER_CAMERA_GRID = booleanPreferencesKey("scanner_camera_grid")
        val SCANNER_FLASH_PREF = stringPreferencesKey("scanner_flash_pref")
        val SCANNER_SAVE_ORIGINAL = booleanPreferencesKey("scanner_save_original")
        val SCANNER_AUTO_SAVE = booleanPreferencesKey("scanner_auto_save")
        val PDF_PAGE_FLIP_ANIM = stringPreferencesKey("pdf_page_flip_anim")
        val PDF_REMEMBER_LAST_PAGE = booleanPreferencesKey("pdf_remember_last_page")
        val PDF_DEFAULT_ZOOM = stringPreferencesKey("pdf_default_zoom")
        val PDF_READING_DIRECTION = stringPreferencesKey("pdf_reading_direction")
        val PDF_NIGHT_MODE = booleanPreferencesKey("pdf_night_mode")
        val PDF_AMOLED_MODE = booleanPreferencesKey("pdf_amoled_mode")
        val PDF_BRIGHTNESS_OVERRIDE = booleanPreferencesKey("pdf_brightness_override")
        val PDF_KEEP_SCREEN_AWAKE = booleanPreferencesKey("pdf_keep_screen_awake")
        val OCR_ENABLED = booleanPreferencesKey("ocr_enabled")
        val OCR_OFFLINE_MODE = booleanPreferencesKey("ocr_offline_mode")
        val OCR_LANGUAGES = stringSetPreferencesKey("ocr_languages")
        val OCR_HANDWRITING = booleanPreferencesKey("ocr_handwriting")
        val OCR_AUTO_INDEX = booleanPreferencesKey("ocr_auto_index")
        val OCR_ACCURACY_MODE = stringPreferencesKey("ocr_accuracy_mode")
        val OCR_BACKGROUND_PROC = booleanPreferencesKey("ocr_background_proc")
        val NOTES_DEFAULT_FONT = stringPreferencesKey("notes_default_font")
        val NOTES_RICH_TEXT = booleanPreferencesKey("notes_rich_text")
        val NOTES_AUTO_SAVE = booleanPreferencesKey("notes_auto_save")
        val NOTES_CONVERT_SCAN = booleanPreferencesKey("notes_convert_scan")
        val NOTES_MARKDOWN = booleanPreferencesKey("notes_markdown")
        val NOTES_SPELL_CHECK = booleanPreferencesKey("notes_spell_check")
        val NOTES_LINE_NUMBERS = booleanPreferencesKey("notes_line_numbers")
        val SEARCH_OCR_TEXT = booleanPreferencesKey("search_ocr_text")
        val SEARCH_NOTES = booleanPreferencesKey("search_notes")
        val SEARCH_ANNOTATIONS = booleanPreferencesKey("search_annotations")
        val SEARCH_FILE_NAMES = booleanPreferencesKey("search_file_names")
        val SEARCH_FOLDER_NAMES = booleanPreferencesKey("search_folder_names")
        val SEARCH_INSTANT = booleanPreferencesKey("search_instant")
        val SEARCH_HIGHLIGHT = booleanPreferencesKey("search_highlight")
        val SEARCH_HISTORY_ENABLED = booleanPreferencesKey("search_history_enabled")
        val WORKSPACE_GROUP_BY = stringPreferencesKey("workspace_group_by")
        val WORKSPACE_SHOW_RECENT = booleanPreferencesKey("workspace_show_recent")
        val WORKSPACE_SHOW_HIDDEN = booleanPreferencesKey("workspace_show_hidden")
        val WORKSPACE_TIMELINE_VIEW = booleanPreferencesKey("workspace_timeline_view")
        val WORKSPACE_DEFAULT_FOLDER = longPreferencesKey("workspace_default_folder")
        val SECURITY_APP_LOCK = booleanPreferencesKey("security_app_lock")
        val SECURITY_LOCK_HIDDEN_FOLDERS = booleanPreferencesKey("security_lock_hidden_folders")
        val SECURITY_HIDE_SENSITIVE = booleanPreferencesKey("security_hide_sensitive")
        val THEME_ACCENT_COLOR = stringPreferencesKey("theme_accent_color")
        val THEME_DYNAMIC_COLORS = booleanPreferencesKey("theme_dynamic_colors")
        val THEME_FONT_SIZE = stringPreferencesKey("theme_font_size")
        val THEME_APP_LANGUAGE = stringPreferencesKey("theme_app_language")
        val THEME_ANIMATIONS = booleanPreferencesKey("theme_animations")
        val THEME_REDUCE_MOTION = booleanPreferencesKey("theme_reduce_motion")
        val BACKUP_DOCUMENTS = booleanPreferencesKey("backup_documents")
        val BACKUP_NOTES = booleanPreferencesKey("backup_notes")
        val BACKUP_SETTINGS = booleanPreferencesKey("backup_settings")
        val NOTIF_SCAN_COMPLETE = booleanPreferencesKey("notif_scan_complete")
        val NOTIF_OCR_COMPLETE = booleanPreferencesKey("notif_ocr_complete")
        val NOTIF_BACKGROUND_TASKS = booleanPreferencesKey("notif_background_tasks")
        val NOTIF_REMINDERS = booleanPreferencesKey("notif_reminders")
        val NOTIF_UPDATES = booleanPreferencesKey("notif_updates")
        val ACCESSIBILITY_LARGER_TEXT = booleanPreferencesKey("accessibility_larger_text")
        val ACCESSIBILITY_HIGH_CONTRAST = booleanPreferencesKey("accessibility_high_contrast")
        val ACCESSIBILITY_SCREEN_READER = booleanPreferencesKey("accessibility_screen_reader")
    }

    // ── Flows ─────────────────────────────────────────────────────────────────
    val isFirstLaunch: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.IS_FIRST_LAUNCH] ?: true }

    val appTheme: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.APP_THEME] ?: "LIGHT" }

    val userName: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.USER_NAME] ?: "" }

    val userAvatarPath: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.USER_AVATAR_PATH] }

    val scannerAutoOcr: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_AUTO_OCR] ?: true }

    val scannerAutoCrop: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_AUTO_CROP] ?: true }

    val scannerDefaultFilter: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_DEFAULT_FILTER] ?: "color" }

    val pdfDefaultView: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_DEFAULT_VIEW] ?: "single" }

    val pdfPageSound: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_PAGE_SOUND] ?: false }

    val vaultTimeoutMinutes: Flow<Int> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.VAULT_TIMEOUT_MINUTES] ?: 10 }

    val vaultLockOnBackground: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.VAULT_LOCK_ON_BACKGROUND] ?: false }

    val biometricEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.BIOMETRIC_ENABLED] ?: true }

    val screenshotProtection: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCREENSHOT_PROTECTION] ?: true }

    val geminiApiKey: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.GEMINI_API_KEY] }

    val clipboardAutoClear: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.CLIPBOARD_AUTO_CLEAR] ?: false }

    val sortOrder: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SORT_ORDER] ?: "date_desc" }

    val viewMode: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.VIEW_MODE] ?: "list" }

    val notesFontSize: Flow<Int> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_FONT_SIZE] ?: 16 }

    // New Flows
    val scannerAutoCapture: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_AUTO_CAPTURE] ?: false }
    val scannerPerspectiveCorrection: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_PERSPECTIVE_CORRECTION] ?: true }
    val scannerSmartRotation: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_SMART_ROTATION] ?: true }
    val scannerMultiPage: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_MULTI_PAGE] ?: false }
    val scannerDefaultQuality: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_DEFAULT_QUALITY] ?: "high" }
    val scannerImageEnhancement: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_IMAGE_ENHANCEMENT] ?: true }
    val scannerDefaultOutput: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_DEFAULT_OUTPUT] ?: "pdf" }
    val scannerHapticFeedback: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_HAPTIC_FEEDBACK] ?: true }
    val scannerCameraGrid: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_CAMERA_GRID] ?: false }
    val scannerFlashPref: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_FLASH_PREF] ?: "auto" }
    val scannerSaveOriginal: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_SAVE_ORIGINAL] ?: false }
    val scannerAutoSave: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SCANNER_AUTO_SAVE] ?: false }
    val pdfPageFlipAnim: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_PAGE_FLIP_ANIM] ?: "slide" }
    val pdfRememberLastPage: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_REMEMBER_LAST_PAGE] ?: true }
    val pdfDefaultZoom: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_DEFAULT_ZOOM] ?: "fit_width" }
    val pdfReadingDirection: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_READING_DIRECTION] ?: "vertical" }
    val pdfNightMode: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_NIGHT_MODE] ?: false }
    val pdfAmoledMode: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_AMOLED_MODE] ?: false }
    val pdfBrightnessOverride: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_BRIGHTNESS_OVERRIDE] ?: false }
    val pdfKeepScreenAwake: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PDF_KEEP_SCREEN_AWAKE] ?: true }
    val ocrEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_ENABLED] ?: true }
    val ocrOfflineMode: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_OFFLINE_MODE] ?: true }
    val ocrLanguages: Flow<Set<String>> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_LANGUAGES] ?: setOf("en") }
    val ocrHandwriting: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_HANDWRITING] ?: false }
    val ocrAutoIndex: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_AUTO_INDEX] ?: true }
    val ocrAccuracyMode: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_ACCURACY_MODE] ?: "balanced" }
    val ocrBackgroundProc: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OCR_BACKGROUND_PROC] ?: true }
    val notesDefaultFont: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_DEFAULT_FONT] ?: "inter" }
    val notesRichText: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_RICH_TEXT] ?: true }
    val notesAutoSave: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_AUTO_SAVE] ?: true }
    val notesConvertScan: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_CONVERT_SCAN] ?: false }
    val notesMarkdown: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_MARKDOWN] ?: false }
    val notesSpellCheck: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_SPELL_CHECK] ?: true }
    val notesLineNumbers: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTES_LINE_NUMBERS] ?: false }
    val searchOcrText: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_OCR_TEXT] ?: true }
    val searchNotes: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_NOTES] ?: true }
    val searchAnnotations: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_ANNOTATIONS] ?: true }
    val searchFileNames: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_FILE_NAMES] ?: true }
    val searchFolderNames: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_FOLDER_NAMES] ?: true }
    val searchInstant: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_INSTANT] ?: true }
    val searchHighlight: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_HIGHLIGHT] ?: true }
    val searchHistoryEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SEARCH_HISTORY_ENABLED] ?: true }
    val workspaceGroupBy: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WORKSPACE_GROUP_BY] ?: "none" }
    val workspaceShowRecent: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WORKSPACE_SHOW_RECENT] ?: true }
    val workspaceShowHidden: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WORKSPACE_SHOW_HIDDEN] ?: false }
    val workspaceTimelineView: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WORKSPACE_TIMELINE_VIEW] ?: false }
    val workspaceDefaultFolder: Flow<Long> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WORKSPACE_DEFAULT_FOLDER] ?: -1L }
    val securityAppLock: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SECURITY_APP_LOCK] ?: false }
    val securityLockHiddenFolders: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SECURITY_LOCK_HIDDEN_FOLDERS] ?: true }
    val securityHideSensitive: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SECURITY_HIDE_SENSITIVE] ?: false }
    val themeAccentColor: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.THEME_ACCENT_COLOR] ?: "amber" }
    val themeDynamicColors: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.THEME_DYNAMIC_COLORS] ?: false }
    val themeFontSize: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.THEME_FONT_SIZE] ?: "medium" }
    val themeAppLanguage: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.THEME_APP_LANGUAGE] ?: "system" }
    val themeAnimations: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.THEME_ANIMATIONS] ?: true }
    val themeReduceMotion: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.THEME_REDUCE_MOTION] ?: false }
    val backupDocuments: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.BACKUP_DOCUMENTS] ?: true }
    val backupNotes: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.BACKUP_NOTES] ?: true }
    val backupSettings: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.BACKUP_SETTINGS] ?: true }
    val notifScanComplete: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIF_SCAN_COMPLETE] ?: true }
    val notifOcrComplete: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIF_OCR_COMPLETE] ?: true }
    val notifBackgroundTasks: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIF_BACKGROUND_TASKS] ?: true }
    val notifReminders: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIF_REMINDERS] ?: true }
    val notifUpdates: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIF_UPDATES] ?: true }
    val accessibilityLargerText: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ACCESSIBILITY_LARGER_TEXT] ?: false }
    val accessibilityHighContrast: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ACCESSIBILITY_HIGH_CONTRAST] ?: false }
    val accessibilityScreenReader: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ACCESSIBILITY_SCREEN_READER] ?: false }

    // ── Writes ────────────────────────────────────────────────────────────────
    suspend fun setFirstLaunchDone() = dataStore.edit { it[Keys.IS_FIRST_LAUNCH] = false }
    suspend fun setAppTheme(theme: String) = dataStore.edit { it[Keys.APP_THEME] = theme }
    suspend fun setUserName(name: String) = dataStore.edit { it[Keys.USER_NAME] = name }
    suspend fun setUserAvatarPath(path: String?) = dataStore.edit {
        if (path != null) it[Keys.USER_AVATAR_PATH] = path
        else it.remove(Keys.USER_AVATAR_PATH)
    }
    suspend fun setScannerFilter(filter: String) = dataStore.edit { it[Keys.SCANNER_DEFAULT_FILTER] = filter }
    suspend fun setScannerAutoCrop(enabled: Boolean) = dataStore.edit { it[Keys.SCANNER_AUTO_CROP] = enabled }
    suspend fun setScannerAutoOcr(enabled: Boolean) = dataStore.edit { it[Keys.SCANNER_AUTO_OCR] = enabled }
    suspend fun setPdfDefaultView(view: String) = dataStore.edit { it[Keys.PDF_DEFAULT_VIEW] = view }
    suspend fun setPdfPageSound(enabled: Boolean) = dataStore.edit { it[Keys.PDF_PAGE_SOUND] = enabled }
    suspend fun setVaultTimeout(minutes: Int) = dataStore.edit { it[Keys.VAULT_TIMEOUT_MINUTES] = minutes }
    suspend fun setVaultLockOnBackground(enabled: Boolean) = dataStore.edit { it[Keys.VAULT_LOCK_ON_BACKGROUND] = enabled }
    suspend fun setBiometricEnabled(enabled: Boolean) = dataStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    suspend fun setScreenshotProtection(enabled: Boolean) = dataStore.edit { it[Keys.SCREENSHOT_PROTECTION] = enabled }
    suspend fun setGeminiApiKey(key: String?) = dataStore.edit {
        if (!key.isNullOrBlank()) it[Keys.GEMINI_API_KEY] = key
        else it.remove(Keys.GEMINI_API_KEY)
    }
    suspend fun setClipboardAutoClear(enabled: Boolean) = dataStore.edit { it[Keys.CLIPBOARD_AUTO_CLEAR] = enabled }
    suspend fun setSortOrder(order: String) = dataStore.edit { it[Keys.SORT_ORDER] = order }
    suspend fun setViewMode(mode: String) = dataStore.edit { it[Keys.VIEW_MODE] = mode }
    suspend fun setNotesFontSize(size: Int) = dataStore.edit { it[Keys.NOTES_FONT_SIZE] = size }

    // New Writes
    suspend fun setScannerAutoCapture(value: Boolean) = dataStore.edit { it[Keys.SCANNER_AUTO_CAPTURE] = value }
    suspend fun setScannerPerspectiveCorrection(value: Boolean) = dataStore.edit { it[Keys.SCANNER_PERSPECTIVE_CORRECTION] = value }
    suspend fun setScannerSmartRotation(value: Boolean) = dataStore.edit { it[Keys.SCANNER_SMART_ROTATION] = value }
    suspend fun setScannerMultiPage(value: Boolean) = dataStore.edit { it[Keys.SCANNER_MULTI_PAGE] = value }
    suspend fun setScannerDefaultQuality(value: String) = dataStore.edit { it[Keys.SCANNER_DEFAULT_QUALITY] = value }
    suspend fun setScannerImageEnhancement(value: Boolean) = dataStore.edit { it[Keys.SCANNER_IMAGE_ENHANCEMENT] = value }
    suspend fun setScannerDefaultOutput(value: String) = dataStore.edit { it[Keys.SCANNER_DEFAULT_OUTPUT] = value }
    suspend fun setScannerHapticFeedback(value: Boolean) = dataStore.edit { it[Keys.SCANNER_HAPTIC_FEEDBACK] = value }
    suspend fun setScannerCameraGrid(value: Boolean) = dataStore.edit { it[Keys.SCANNER_CAMERA_GRID] = value }
    suspend fun setScannerFlashPref(value: String) = dataStore.edit { it[Keys.SCANNER_FLASH_PREF] = value }
    suspend fun setScannerSaveOriginal(value: Boolean) = dataStore.edit { it[Keys.SCANNER_SAVE_ORIGINAL] = value }
    suspend fun setScannerAutoSave(value: Boolean) = dataStore.edit { it[Keys.SCANNER_AUTO_SAVE] = value }
    suspend fun setPdfPageFlipAnim(value: String) = dataStore.edit { it[Keys.PDF_PAGE_FLIP_ANIM] = value }
    suspend fun setPdfRememberLastPage(value: Boolean) = dataStore.edit { it[Keys.PDF_REMEMBER_LAST_PAGE] = value }
    suspend fun setPdfDefaultZoom(value: String) = dataStore.edit { it[Keys.PDF_DEFAULT_ZOOM] = value }
    suspend fun setPdfReadingDirection(value: String) = dataStore.edit { it[Keys.PDF_READING_DIRECTION] = value }
    suspend fun setPdfNightMode(value: Boolean) = dataStore.edit { it[Keys.PDF_NIGHT_MODE] = value }
    suspend fun setPdfAmoledMode(value: Boolean) = dataStore.edit { it[Keys.PDF_AMOLED_MODE] = value }
    suspend fun setPdfBrightnessOverride(value: Boolean) = dataStore.edit { it[Keys.PDF_BRIGHTNESS_OVERRIDE] = value }
    suspend fun setPdfKeepScreenAwake(value: Boolean) = dataStore.edit { it[Keys.PDF_KEEP_SCREEN_AWAKE] = value }
    suspend fun setOcrEnabled(value: Boolean) = dataStore.edit { it[Keys.OCR_ENABLED] = value }
    suspend fun setOcrOfflineMode(value: Boolean) = dataStore.edit { it[Keys.OCR_OFFLINE_MODE] = value }
    suspend fun setOcrLanguages(value: Set<String>) = dataStore.edit { it[Keys.OCR_LANGUAGES] = value }
    suspend fun setOcrHandwriting(value: Boolean) = dataStore.edit { it[Keys.OCR_HANDWRITING] = value }
    suspend fun setOcrAutoIndex(value: Boolean) = dataStore.edit { it[Keys.OCR_AUTO_INDEX] = value }
    suspend fun setOcrAccuracyMode(value: String) = dataStore.edit { it[Keys.OCR_ACCURACY_MODE] = value }
    suspend fun setOcrBackgroundProc(value: Boolean) = dataStore.edit { it[Keys.OCR_BACKGROUND_PROC] = value }
    suspend fun setNotesDefaultFont(value: String) = dataStore.edit { it[Keys.NOTES_DEFAULT_FONT] = value }
    suspend fun setNotesRichText(value: Boolean) = dataStore.edit { it[Keys.NOTES_RICH_TEXT] = value }
    suspend fun setNotesAutoSave(value: Boolean) = dataStore.edit { it[Keys.NOTES_AUTO_SAVE] = value }
    suspend fun setNotesConvertScan(value: Boolean) = dataStore.edit { it[Keys.NOTES_CONVERT_SCAN] = value }
    suspend fun setNotesMarkdown(value: Boolean) = dataStore.edit { it[Keys.NOTES_MARKDOWN] = value }
    suspend fun setNotesSpellCheck(value: Boolean) = dataStore.edit { it[Keys.NOTES_SPELL_CHECK] = value }
    suspend fun setNotesLineNumbers(value: Boolean) = dataStore.edit { it[Keys.NOTES_LINE_NUMBERS] = value }
    suspend fun setSearchOcrText(value: Boolean) = dataStore.edit { it[Keys.SEARCH_OCR_TEXT] = value }
    suspend fun setSearchNotes(value: Boolean) = dataStore.edit { it[Keys.SEARCH_NOTES] = value }
    suspend fun setSearchAnnotations(value: Boolean) = dataStore.edit { it[Keys.SEARCH_ANNOTATIONS] = value }
    suspend fun setSearchFileNames(value: Boolean) = dataStore.edit { it[Keys.SEARCH_FILE_NAMES] = value }
    suspend fun setSearchFolderNames(value: Boolean) = dataStore.edit { it[Keys.SEARCH_FOLDER_NAMES] = value }
    suspend fun setSearchInstant(value: Boolean) = dataStore.edit { it[Keys.SEARCH_INSTANT] = value }
    suspend fun setSearchHighlight(value: Boolean) = dataStore.edit { it[Keys.SEARCH_HIGHLIGHT] = value }
    suspend fun setSearchHistoryEnabled(value: Boolean) = dataStore.edit { it[Keys.SEARCH_HISTORY_ENABLED] = value }
    suspend fun setWorkspaceGroupBy(value: String) = dataStore.edit { it[Keys.WORKSPACE_GROUP_BY] = value }
    suspend fun setWorkspaceShowRecent(value: Boolean) = dataStore.edit { it[Keys.WORKSPACE_SHOW_RECENT] = value }
    suspend fun setWorkspaceShowHidden(value: Boolean) = dataStore.edit { it[Keys.WORKSPACE_SHOW_HIDDEN] = value }
    suspend fun setWorkspaceTimelineView(value: Boolean) = dataStore.edit { it[Keys.WORKSPACE_TIMELINE_VIEW] = value }
    suspend fun setWorkspaceDefaultFolder(value: Long) = dataStore.edit { it[Keys.WORKSPACE_DEFAULT_FOLDER] = value }
    suspend fun setSecurityAppLock(value: Boolean) = dataStore.edit { it[Keys.SECURITY_APP_LOCK] = value }
    suspend fun setSecurityLockHiddenFolders(value: Boolean) = dataStore.edit { it[Keys.SECURITY_LOCK_HIDDEN_FOLDERS] = value }
    suspend fun setSecurityHideSensitive(value: Boolean) = dataStore.edit { it[Keys.SECURITY_HIDE_SENSITIVE] = value }
    suspend fun setThemeAccentColor(value: String) = dataStore.edit { it[Keys.THEME_ACCENT_COLOR] = value }
    suspend fun setThemeDynamicColors(value: Boolean) = dataStore.edit { it[Keys.THEME_DYNAMIC_COLORS] = value }
    suspend fun setThemeFontSize(value: String) = dataStore.edit { it[Keys.THEME_FONT_SIZE] = value }
    suspend fun setThemeAppLanguage(value: String) = dataStore.edit { it[Keys.THEME_APP_LANGUAGE] = value }
    suspend fun setThemeAnimations(value: Boolean) = dataStore.edit { it[Keys.THEME_ANIMATIONS] = value }
    suspend fun setThemeReduceMotion(value: Boolean) = dataStore.edit { it[Keys.THEME_REDUCE_MOTION] = value }
    suspend fun setBackupDocuments(value: Boolean) = dataStore.edit { it[Keys.BACKUP_DOCUMENTS] = value }
    suspend fun setBackupNotes(value: Boolean) = dataStore.edit { it[Keys.BACKUP_NOTES] = value }
    suspend fun setBackupSettings(value: Boolean) = dataStore.edit { it[Keys.BACKUP_SETTINGS] = value }
    suspend fun setNotifScanComplete(value: Boolean) = dataStore.edit { it[Keys.NOTIF_SCAN_COMPLETE] = value }
    suspend fun setNotifOcrComplete(value: Boolean) = dataStore.edit { it[Keys.NOTIF_OCR_COMPLETE] = value }
    suspend fun setNotifBackgroundTasks(value: Boolean) = dataStore.edit { it[Keys.NOTIF_BACKGROUND_TASKS] = value }
    suspend fun setNotifReminders(value: Boolean) = dataStore.edit { it[Keys.NOTIF_REMINDERS] = value }
    suspend fun setNotifUpdates(value: Boolean) = dataStore.edit { it[Keys.NOTIF_UPDATES] = value }
    suspend fun setAccessibilityLargerText(value: Boolean) = dataStore.edit { it[Keys.ACCESSIBILITY_LARGER_TEXT] = value }
    suspend fun setAccessibilityHighContrast(value: Boolean) = dataStore.edit { it[Keys.ACCESSIBILITY_HIGH_CONTRAST] = value }
    suspend fun setAccessibilityScreenReader(value: Boolean) = dataStore.edit { it[Keys.ACCESSIBILITY_SCREEN_READER] = value }
}
