package com.paperflow.app.presentation.onboarding;

import com.paperflow.app.data.local.datastore.PreferencesDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<PreferencesDataStore> prefsProvider;

  public OnboardingViewModel_Factory(Provider<PreferencesDataStore> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<PreferencesDataStore> prefsProvider) {
    return new OnboardingViewModel_Factory(prefsProvider);
  }

  public static OnboardingViewModel newInstance(PreferencesDataStore prefs) {
    return new OnboardingViewModel(prefs);
  }
}
