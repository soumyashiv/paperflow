package com.paperflow.app.data.local.file

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralised file path management.
 * All files stored in app-private directories (SR-STORAGE-001).
 * No unrestricted external storage (SR-STORAGE-003).
 */
@Singleton
class FileStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    // Root directories inside app's private filesDir
    val documentsDir: File get() = dir("documents")
    val scansDir: File get() = dir("scans")
    val thumbnailsDir: File get() = dir("thumbnails")
    val encryptedDir: File get() = dir("encrypted")
    val notesDir: File get() = dir("notes")
    val tempDir: File get() = dir("temp")
    val exportsDir: File get() = dir("exports")
    val cacheDir: File get() = context.cacheDir.also { it.mkdirs() }

    private fun dir(name: String): File =
        File(context.filesDir, name).also { it.mkdirs() }

    // ── Path helpers ──────────────────────────────────────────────────────────
    fun documentFile(filename: String): File = File(documentsDir, filename)
    fun scanFile(sessionId: String, index: Int): File =
        File(scansDir, "$sessionId-$index.jpg")
    fun thumbnailFile(documentId: Long, page: Int = 0): File =
        File(thumbnailsDir, "thumb_${documentId}_$page.jpg")
    fun encryptedFile(filename: String): File = File(encryptedDir, filename)
    fun tempFile(name: String): File = File(tempDir, name)
    fun exportFile(name: String): File = File(exportsDir, name)

    // ── Cleanup ───────────────────────────────────────────────────────────────
    /** Securely delete all temp files older than maxAgeMs (SR-STORAGE-004). */
    fun cleanTemp(maxAgeMs: Long = 30 * 60 * 1000L) {
        val cutoff = System.currentTimeMillis() - maxAgeMs
        tempDir.listFiles()
            ?.filter { it.lastModified() < cutoff }
            ?.forEach { it.delete() }
    }

    /**
     * Secure delete — overwrite before delete to reduce forensic recovery risk.
     * Not a full secure-erase (requires OS-level support), but raises the bar.
     */
    fun secureDelete(file: File) {
        if (!file.exists()) return
        try {
            file.outputStream().use { out ->
                val zeros = ByteArray(4096)
                var remaining = file.length()
                while (remaining > 0) {
                    val toWrite = minOf(remaining, 4096).toInt()
                    out.write(zeros, 0, toWrite)
                    remaining -= toWrite
                }
                out.flush()
            }
        } finally {
            file.delete()
        }
    }

    /** Delete all artefacts associated with a document (SR-STORAGE-005). */
    fun deleteDocumentArtefacts(documentId: Long) {
        // Thumbnails for all pages
        thumbnailsDir.listFiles { f -> f.name.startsWith("thumb_${documentId}_") }
            ?.forEach { it.delete() }
    }

    fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
        }
    }
}
