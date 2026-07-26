package com.paperflow.app.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paperflow.app.presentation.aichat.AIChatScreen
import com.paperflow.app.presentation.annotations.AnnotationScreen
import com.paperflow.app.presentation.convert.ConvertScreen
import com.paperflow.app.presentation.home.HomeScreen
import com.paperflow.app.presentation.notes.NoteEditorScreen
import com.paperflow.app.presentation.notes.NotesScreen
import com.paperflow.app.presentation.notifications.NotificationsScreen
import com.paperflow.app.presentation.onboarding.GetStartedScreen
import com.paperflow.app.presentation.pdfviewer.PDFViewerScreen
import com.paperflow.app.presentation.scanner.EditScanScreen
import com.paperflow.app.presentation.scanner.IDCardScanScreen // kept for deep-link access; not exposed via Quick Actions
import com.paperflow.app.presentation.scanner.ScannerScreen
import com.paperflow.app.presentation.search.SearchScreen
import com.paperflow.app.presentation.settings.*
import com.paperflow.app.presentation.share.PrintOptionsScreen
import com.paperflow.app.presentation.share.ShareOptionsScreen
import com.paperflow.app.presentation.upload.UploadScreen
import com.paperflow.app.presentation.vault.VaultAuthScreen
import com.paperflow.app.presentation.vault.VaultContentsScreen
import com.paperflow.app.presentation.workspace.DocumentDetailsScreen
import com.paperflow.app.presentation.workspace.WorkspaceScreen

/**
 * App-level navigation graph.
 * All transitions use shared spring physics for consistent motion feel.
 * Enter: fade+slide from bottom (24dp).
 * Exit: fade out. PopEnter: no anim. PopExit: slide down+fade.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.GetStarted.route,
    modifier: Modifier = Modifier,
) {
    val slideEnter = slideInVertically(
        initialOffsetY = { it / 12 },
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
    ) + fadeIn(animationSpec = spring())

    val slideExit = slideOutVertically(
        targetOffsetY = { -it / 16 },
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
    ) + fadeOut()

    val popExit = slideOutVertically(
        targetOffsetY = { it / 8 },
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
    ) + fadeOut()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideEnter },
        exitTransition = { slideExit },
        popEnterTransition = { fadeIn() },
        popExitTransition = { popExit },
    ) {

        // ── Onboarding ───────────────────────────────────────────────────────
        composable(Routes.GetStarted.route) {
            GetStartedScreen(onGetStarted = {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.GetStarted.route) { inclusive = true }
                }
            })
        }

        // ── Home ─────────────────────────────────────────────────────────────
        composable(Routes.Home.route) {
            HomeScreen(
                navController = navController,
                onScanClick = { navController.navigate(Routes.Scanner.route) },
                onDocumentClick = { docId -> navController.navigate(Routes.PDFViewer.withId(docId)) },
                onSearchClick = { navController.navigate(Routes.Search.withQuery()) },
                onNotificationsClick = { navController.navigate(Routes.Notifications.route) },
                onAiChatClick = { navController.navigate(Routes.AIChat.withDocument()) },
                onUploadClick = { navController.navigate(Routes.Upload.route) },
                onConvertClick = { navController.navigate(Routes.Convert.withId(0L)) },
            )
        }

        // ── Workspace ────────────────────────────────────────────────────────
        composable(Routes.Workspace.route) {
            WorkspaceScreen(
                navController = navController,
                onDocumentClick = { docId -> navController.navigate(Routes.PDFViewer.withId(docId)) },
                onDocumentDetails = { docId -> navController.navigate(Routes.DocumentDetails.withId(docId)) },
                onFolderClick = { folderId -> navController.navigate(Routes.FolderContents.withId(folderId)) },
                onVaultClick = { navController.navigate(Routes.VaultAuth.route) },
                onScanClick = { navController.navigate(Routes.Scanner.route) },
            )
        }

        // ── Folder Contents ──────────────────────────────────────────────────
        composable(
            route = Routes.FolderContents.route,
            arguments = listOf(navArgument(Routes.FolderContents.ARG_FOLDER_ID) { type = NavType.LongType }),
        ) { back ->
            val folderId = back.arguments!!.getLong(Routes.FolderContents.ARG_FOLDER_ID)
            WorkspaceScreen(
                navController = navController,
                folderId = folderId,
                onDocumentClick = { docId -> navController.navigate(Routes.PDFViewer.withId(docId)) },
                onDocumentDetails = { docId -> navController.navigate(Routes.DocumentDetails.withId(docId)) },
                onFolderClick = { id -> navController.navigate(Routes.FolderContents.withId(id)) },
                onVaultClick = { navController.navigate(Routes.VaultAuth.route) },
                onScanClick = { navController.navigate(Routes.Scanner.route) },
            )
        }

        // ── Scanner ──────────────────────────────────────────────────────────
        composable(Routes.Scanner.route) {
            ScannerScreen(
                onClose = { navController.popBackStack() },
                onScanComplete = { sessionId ->
                    navController.navigate(Routes.EditScan.withId(sessionId)) {
                        popUpTo(Routes.Scanner.route) { inclusive = true }
                    }
                },
            )
        }

        // ── Edit Scan ────────────────────────────────────────────────────────
        composable(
            route = Routes.EditScan.route,
            arguments = listOf(navArgument(Routes.EditScan.ARG_SESSION_ID) { type = NavType.StringType }),
        ) { back ->
            val sessionId = back.arguments!!.getString(Routes.EditScan.ARG_SESSION_ID)!!
            EditScanScreen(
                sessionId = sessionId,
                onClose = { navController.popBackStack() },
                onSaved = { docId ->
                    navController.navigate(Routes.PDFViewer.withId(docId)) {
                        popUpTo(Routes.Home.route)
                    }
                },
            )
        }

        // ── ID Card Scan ─────────────────────────────────────────────────────
        composable(Routes.IDCardScan.route) {
            IDCardScanScreen(onClose = { navController.popBackStack() })
        }

        // ── PDF Viewer ───────────────────────────────────────────────────────
        composable(
            route = Routes.PDFViewer.route,
            arguments = listOf(
                navArgument(Routes.PDFViewer.ARG_DOC_ID) { type = NavType.LongType },
                navArgument(Routes.PDFViewer.ARG_PAGE) { type = NavType.IntType; defaultValue = 0 },
            ),
        ) { back ->
            val docId = back.arguments!!.getLong(Routes.PDFViewer.ARG_DOC_ID)
            val page = back.arguments!!.getInt(Routes.PDFViewer.ARG_PAGE)
            PDFViewerScreen(
                documentId = docId,
                initialPage = page,
                onBack = { navController.popBackStack() },
                onAnnotate = { pageIndex -> navController.navigate(Routes.Annotations.withArgs(docId, pageIndex)) },
                onShare = { navController.navigate(Routes.ShareOptions.withId(docId)) },
                onPrint = { navController.navigate(Routes.PrintOptions.withId(docId)) },
                onConvert = { navController.navigate(Routes.Convert.withId(docId)) },
                onDetails = { navController.navigate(Routes.DocumentDetails.withId(docId)) },
            )
        }

        // ── Document Details ─────────────────────────────────────────────────
        composable(
            route = Routes.DocumentDetails.route,
            arguments = listOf(navArgument(Routes.DocumentDetails.ARG_DOC_ID) { type = NavType.LongType }),
        ) { back ->
            val docId = back.arguments!!.getLong(Routes.DocumentDetails.ARG_DOC_ID)
            DocumentDetailsScreen(
                documentId = docId,
                onBack = { navController.popBackStack() },
                onOpen = { navController.navigate(Routes.PDFViewer.withId(docId)) },
                onAnnotate = { navController.navigate(Routes.Annotations.withArgs(docId, 0)) },
                onShare = { navController.navigate(Routes.ShareOptions.withId(docId)) },
                onConvertToNote = { /* navigated after conversion */ },
            )
        }

        // ── Annotations ──────────────────────────────────────────────────────
        composable(
            route = Routes.Annotations.route,
            arguments = listOf(
                navArgument(Routes.Annotations.ARG_DOC_ID) { type = NavType.LongType },
                navArgument(Routes.Annotations.ARG_PAGE_INDEX) { type = NavType.IntType },
            ),
        ) { back ->
            val docId = back.arguments!!.getLong(Routes.Annotations.ARG_DOC_ID)
            val pageIndex = back.arguments!!.getInt(Routes.Annotations.ARG_PAGE_INDEX)
            AnnotationScreen(
                documentId = docId,
                pageIndex = pageIndex,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Notes ─────────────────────────────────────────────────────────────
        composable(Routes.Notes.route) {
            NotesScreen(
                navController = navController,
                onNoteClick = { noteId -> navController.navigate(Routes.NoteEditor.withId(noteId)) },
                onNewNote = { navController.navigate(Routes.NoteEditor.newNote()) },
                onScanClick = { navController.navigate(Routes.Scanner.route) },
            )
        }

        // ── Note Editor ──────────────────────────────────────────────────────
        composable(
            route = Routes.NoteEditor.route,
            arguments = listOf(navArgument(Routes.NoteEditor.ARG_NOTE_ID) { type = NavType.LongType }),
        ) { back ->
            val noteId = back.arguments!!.getLong(Routes.NoteEditor.ARG_NOTE_ID)
            NoteEditorScreen(
                noteId = noteId,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Search ────────────────────────────────────────────────────────────
        composable(
            route = Routes.Search.route,
            arguments = listOf(navArgument(Routes.Search.ARG_QUERY) { type = NavType.StringType; defaultValue = "" }),
        ) { back ->
            val initQuery = back.arguments?.getString(Routes.Search.ARG_QUERY) ?: ""
            SearchScreen(
                initialQuery = initQuery,
                onBack = { navController.popBackStack() },
                onResultClick = { result ->
                    when (result.resultType) {
                        com.paperflow.app.domain.model.SearchResultType.PAGE_TEXT,
                        com.paperflow.app.domain.model.SearchResultType.DOCUMENT ->
                            navController.navigate(Routes.PDFViewer.withId(result.documentId))
                        com.paperflow.app.domain.model.SearchResultType.NOTE ->
                            navController.navigate(Routes.NoteEditor.withId(result.documentId))
                        com.paperflow.app.domain.model.SearchResultType.FOLDER ->
                            navController.navigate(Routes.FolderContents.withId(result.documentId))
                        else -> {}
                    }
                },
            )
        }

        // ── Vault ─────────────────────────────────────────────────────────────
        composable(Routes.VaultAuth.route) {
            VaultAuthScreen(
                onAuthenticated = { navController.navigate(Routes.VaultContents.route) { popUpTo(Routes.VaultAuth.route) { inclusive = true } } },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.VaultContents.route) {
            VaultContentsScreen(
                navController = navController,
                onDocumentClick = { docId -> navController.navigate(Routes.PDFViewer.withId(docId)) },
                onBack = { navController.popBackStack() },
            )
        }

        // ── Settings ─────────────────────────────────────────────────────────
        composable(Routes.Settings.route) {
            SettingsScreen(
                navController = navController,
                onScanClick = { navController.navigate(Routes.Scanner.route) },
            )
        }
        composable(Routes.SettingsGeneral.route) { GeneralSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsScanner.route) { ScannerSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsPDFReader.route) { PDFReaderSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsOCR.route) { OCRSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsNotes.route) { NotesSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsSearch.route) { SearchSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsWorkspace.route) { WorkspaceSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsSecurity.route) { SecuritySettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsThemes.route) { ThemesSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsStorage.route) { StorageSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsBackup.route) { BackupRestoreScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsNotifications.route) { NotificationsSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsAccessibility.route) { AccessibilitySettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsAbout.route) { AboutScreen(onBack = { navController.popBackStack() }) }
        composable(Routes.SettingsDeveloper.route) { DeveloperSettingsScreen(onBack = { navController.popBackStack() }) }

        // ── AI Chat ───────────────────────────────────────────────────────────
        composable(
            route = Routes.AIChat.route,
            arguments = listOf(navArgument(Routes.AIChat.ARG_DOC_ID) { type = NavType.LongType; defaultValue = -1L }),
        ) { back ->
            val docId = back.arguments?.getLong(Routes.AIChat.ARG_DOC_ID)?.takeIf { it >= 0 }
            AIChatScreen(documentId = docId, onBack = { navController.popBackStack() })
        }

        // ── Notifications ─────────────────────────────────────────────────────
        composable(Routes.Notifications.route) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        // ── Convert ───────────────────────────────────────────────────────────
        composable(
            route = Routes.Convert.route,
            arguments = listOf(navArgument(Routes.Convert.ARG_DOC_ID) { type = NavType.LongType }),
        ) { back ->
            val docId = back.arguments!!.getLong(Routes.Convert.ARG_DOC_ID)
            ConvertScreen(documentId = docId, onBack = { navController.popBackStack() })
        }

        // ── Share/Save/Print ─────────────────────────────────────────────────
        composable(
            route = Routes.ShareOptions.route,
            arguments = listOf(navArgument(Routes.ShareOptions.ARG_DOC_ID) { type = NavType.LongType }),
        ) { back ->
            val docId = back.arguments!!.getLong(Routes.ShareOptions.ARG_DOC_ID)
            ShareOptionsScreen(documentId = docId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.PrintOptions.route,
            arguments = listOf(navArgument(Routes.PrintOptions.ARG_DOC_ID) { type = NavType.LongType }),
        ) { back ->
            val docId = back.arguments!!.getLong(Routes.PrintOptions.ARG_DOC_ID)
            PrintOptionsScreen(documentId = docId, onBack = { navController.popBackStack() })
        }

        // ── Upload / Import ───────────────────────────────────────────────────
        composable(Routes.Upload.route) {
            UploadScreen(onBack = { navController.popBackStack() }, onDone = { navController.popBackStack() })
        }
        composable(Routes.Import.route) {
            UploadScreen(
                onBack = { navController.popBackStack() },
                onDone = { navController.navigate(Routes.Workspace.route) { popUpTo(Routes.Home.route) } },
            )
        }

        // ── Help Center ───────────────────────────────────────────────────────
        composable(Routes.HelpCenter.route) { HelpCenterScreen(onBack = { navController.popBackStack() }) }
    }
}
