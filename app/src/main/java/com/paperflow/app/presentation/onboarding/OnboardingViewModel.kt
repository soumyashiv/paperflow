package com.paperflow.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.datastore.PreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: PreferencesDataStore,
) : ViewModel() {

    /**
     * If this is not the first launch, skip onboarding immediately.
     * [skip] is called on the calling coroutine context (Main).
     */
    fun checkFirstLaunch(skip: () -> Unit) {
        viewModelScope.launch {
            val isFirst = prefs.isFirstLaunch.first()
            if (!isFirst) skip()
        }
    }

    fun markFirstLaunchDone() {
        viewModelScope.launch { prefs.setFirstLaunchDone() }
    }
}
