package com.paperflow.app.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe navigation routes for PaperFlow.
 * All argument names match the deep link patterns defined in AndroidManifest.
 */
sealed class Routes(val route: String) {

    // ── Onboarding ──────────────────────────────────────────────────────────
    data object GetStarted : Routes("get_started")

    // ── Main Tabs ───────────────────────────────────────────────────────────
    data object Home : Routes("home")
    data object Workspace : Routes("workspace")
    data object Notes : Routes("notes")
    data object Settings : Routes("settings")

    // ── Scanner ─────────────────────────────────────────────────────────────
    data object Scanner : Routes("scanner")
    data object EditScan : Routes("edit_scan/{scanSessionId}") {
        fun withId(scanSessionId: String) = "edit_scan/$scanSessionId"
        const val ARG_SESSION_ID = "scanSessionId"
    }
    data object IDCardScan : Routes("id_card_scan")

    // ── Documents ────────────────────────────────────────────────────────────
    data object DocumentDetails : Routes("document_details/{documentId}") {
        fun withId(documentId: Long) = "document_details/$documentId"
        const val ARG_DOC_ID = "documentId"
    }
    data object PDFViewer : Routes("pdf_viewer/{documentId}?page={page}") {
        fun withId(documentId: Long, page: Int = 0) = "pdf_viewer/$documentId?page=$page"
        const val ARG_DOC_ID = "documentId"
        const val ARG_PAGE = "page"
    }

    // ── Workspace ────────────────────────────────────────────────────────────
    data object FolderContents : Routes("folder/{folderId}") {
        fun withId(folderId: Long) = "folder/$folderId"
        const val ARG_FOLDER_ID = "folderId"
    }

    // ── Notes ────────────────────────────────────────────────────────────────
    data object NoteEditor : Routes("note_editor/{noteId}") {
        fun withId(noteId: Long) = "note_editor/$noteId"
        fun newNote() = "note_editor/-1"
        const val ARG_NOTE_ID = "noteId"
    }

    // ── Annotations ──────────────────────────────────────────────────────────
    data object Annotations : Routes("annotations/{documentId}/{pageIndex}") {
        fun withArgs(documentId: Long, pageIndex: Int) = "annotations/$documentId/$pageIndex"
        const val ARG_DOC_ID = "documentId"
        const val ARG_PAGE_INDEX = "pageIndex"
    }

    // ── Search ────────────────────────────────────────────────────────────────
    data object Search : Routes("search?query={query}") {
        fun withQuery(query: String = "") = "search?query=$query"
        const val ARG_QUERY = "query"
    }

    // ── Vault ─────────────────────────────────────────────────────────────────
    data object VaultAuth : Routes("vault_auth")
    data object VaultContents : Routes("vault_contents")

    // ── File Operations ───────────────────────────────────────────────────────
    data object Upload : Routes("upload")
    data object Import : Routes("import")
    data object ShareOptions : Routes("share_options/{documentId}") {
        fun withId(documentId: Long) = "share_options/$documentId"
        const val ARG_DOC_ID = "documentId"
    }
    data object SaveOptions : Routes("save_options/{documentId}") {
        fun withId(documentId: Long) = "save_options/$documentId"
        const val ARG_DOC_ID = "documentId"
    }
    data object PrintOptions : Routes("print_options/{documentId}") {
        fun withId(documentId: Long) = "print_options/$documentId"
        const val ARG_DOC_ID = "documentId"
    }
    data object PrintPreview : Routes("print_preview/{documentId}") {
        fun withId(documentId: Long) = "print_preview/$documentId"
        const val ARG_DOC_ID = "documentId"
    }
    data object Convert : Routes("convert/{documentId}") {
        fun withId(documentId: Long) = "convert/$documentId"
        const val ARG_DOC_ID = "documentId"
    }

    // ── Settings Sub-Screens ──────────────────────────────────────────────────
    data object SettingsGeneral : Routes("settings/general")
    data object SettingsScanner : Routes("settings/scanner")
    data object SettingsPDFReader : Routes("settings/pdf_reader")
    data object SettingsOCR : Routes("settings/ocr")
    data object SettingsNotes : Routes("settings/notes")
    data object SettingsSearch : Routes("settings/search")
    data object SettingsWorkspace : Routes("settings/workspace")
    data object SettingsSecurity : Routes("settings/security")
    data object SettingsThemes : Routes("settings/themes")
    data object SettingsStorage : Routes("settings/storage")
    data object SettingsBackup : Routes("settings/backup")
    data object SettingsNotifications : Routes("settings/notifications")
    data object SettingsAccessibility : Routes("settings/accessibility")
    data object SettingsAbout : Routes("settings/about")
    data object SettingsDeveloper : Routes("settings/developer")

    // ── AI Chat ───────────────────────────────────────────────────────────────
    data object AIChat : Routes("ai_chat?documentId={documentId}") {
        fun withDocument(documentId: Long? = null) =
            if (documentId != null) "ai_chat?documentId=$documentId" else "ai_chat"
        const val ARG_DOC_ID = "documentId"
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    data object Notifications : Routes("notifications")

    // ── Help Center ───────────────────────────────────────────────────────────
    data object HelpCenter : Routes("help_center")
}

/** Bottom nav destinations — the 4 permanent tabs (FAB is 5th, not a tab) */
enum class BottomNavDestination(
    val route: String,
    val labelRes: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector,
) {
    HOME(
        route = Routes.Home.route,
        labelRes = com.paperflow.app.R.string.nav_home,
        unselectedIcon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home,
    ),
    WORKSPACE(
        route = Routes.Workspace.route,
        labelRes = com.paperflow.app.R.string.nav_workspace,
        unselectedIcon = Icons.Outlined.Folder,
        selectedIcon = Icons.Filled.Folder,
    ),
    NOTES(
        route = Routes.Notes.route,
        labelRes = com.paperflow.app.R.string.nav_notes,
        unselectedIcon = Icons.Outlined.Edit,
        selectedIcon = Icons.Filled.Edit,
    ),
    SETTINGS(
        route = Routes.Settings.route,
        labelRes = com.paperflow.app.R.string.nav_settings,
        unselectedIcon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
    ),
}
