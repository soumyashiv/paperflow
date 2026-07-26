package com.paperflow.app.presentation.settings;

import com.paperflow.app.data.local.datastore.PreferencesDataStore;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.domain.repository.DocumentRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<PreferencesDataStore> prefsProvider;

  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<FileStorage> storageProvider;

  public SettingsViewModel_Factory(Provider<PreferencesDataStore> prefsProvider,
      Provider<DocumentRepository> documentRepoProvider, Provider<FileStorage> storageProvider) {
    this.prefsProvider = prefsProvider;
    this.documentRepoProvider = documentRepoProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(prefsProvider.get(), documentRepoProvider.get(), storageProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<PreferencesDataStore> prefsProvider,
      Provider<DocumentRepository> documentRepoProvider, Provider<FileStorage> storageProvider) {
    return new SettingsViewModel_Factory(prefsProvider, documentRepoProvider, storageProvider);
  }

  public static SettingsViewModel newInstance(PreferencesDataStore prefs,
      DocumentRepository documentRepo, FileStorage storage) {
    return new SettingsViewModel(prefs, documentRepo, storage);
  }
}
