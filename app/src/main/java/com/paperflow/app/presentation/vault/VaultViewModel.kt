package com.paperflow.app.presentation.vault

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.datastore.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class VaultAuthState { IDLE, AUTHENTICATING, SUCCESS, FAILED, NOT_ENROLLED }

data class VaultUiState(
    val authState: VaultAuthState = VaultAuthState.IDLE,
    val biometricAvailable: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: PreferencesDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(VaultUiState())
    val state: StateFlow<VaultUiState> = _state.asStateFlow()

    init {
        checkBiometricAvailability()
    }

    private fun checkBiometricAvailability() {
        val bm = BiometricManager.from(context)
        val canAuth = bm.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        _state.update {
            it.copy(
                biometricAvailable = canAuth == BiometricManager.BIOMETRIC_SUCCESS,
                authState = if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) VaultAuthState.NOT_ENROLLED else VaultAuthState.IDLE,
            )
        }
    }

    /** Launch biometric prompt. Must be called from a FragmentActivity context. */
    fun authenticate(activity: FragmentActivity) {
        _state.update { it.copy(authState = VaultAuthState.AUTHENTICATING) }

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    _state.update { it.copy(authState = VaultAuthState.SUCCESS, errorMessage = null) }
                }

                override fun onAuthenticationFailed() {
                    _state.update { it.copy(authState = VaultAuthState.FAILED, errorMessage = "Authentication failed. Try again.") }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    val msg = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> null // User dismissed — don't show error
                        BiometricPrompt.ERROR_LOCKOUT -> "Too many attempts. Try again in 30 seconds."
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> "Biometrics locked. Use device PIN."
                        else -> errString.toString()
                    }
                    _state.update { it.copy(authState = VaultAuthState.IDLE, errorMessage = msg) }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Vault")
            .setSubtitle("Authenticate to access your protected documents")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }

    fun resetAuth() = _state.update { it.copy(authState = VaultAuthState.IDLE, errorMessage = null) }
}
