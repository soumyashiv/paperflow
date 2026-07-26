package com.paperflow.app.data.local.security

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    /**
     * Encrypts a source file and writes the encrypted content to the destination file.
     * SR-SEC-002: AES-256 encryption
     */
    fun encryptFile(sourceFile: File, destinationFile: File) {
        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            context,
            destinationFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        sourceFile.inputStream().use { inputStream ->
            encryptedFile.openFileOutput().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    /**
     * Decrypts an encrypted file and writes the plaintext content to the destination file.
     */
    fun decryptFile(encryptedFile: File, destinationFile: File) {
        if (destinationFile.exists()) {
            destinationFile.delete()
        }

        val encrypted = EncryptedFile.Builder(
            context,
            encryptedFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encrypted.openFileInput().use { inputStream ->
            destinationFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
