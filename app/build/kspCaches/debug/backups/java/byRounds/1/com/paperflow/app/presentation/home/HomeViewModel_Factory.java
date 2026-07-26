package com.paperflow.app.presentation.home;

import com.paperflow.app.data.local.datastore.PreferencesDataStore;
import com.paperflow.app.domain.usecase.DeleteDocumentUseCase;
import com.paperflow.app.domain.usecase.GetFoldersUseCase;
import com.paperflow.app.domain.usecase.GetRecentDocumentsUseCase;
import com.paperflow.app.domain.usecase.GetStorageInfoUseCase;
import com.paperflow.app.domain.usecase.ToggleFavoriteUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetRecentDocumentsUseCase> getRecentDocumentsProvider;

  private final Provider<GetFoldersUseCase> getFoldersProvider;

  private final Provider<GetStorageInfoUseCase> getStorageInfoProvider;

  private final Provider<ToggleFavoriteUseCase> toggleFavoriteProvider;

  private final Provider<DeleteDocumentUseCase> deleteDocumentProvider;

  private final Provider<PreferencesDataStore> prefsProvider;

  public HomeViewModel_Factory(Provider<GetRecentDocumentsUseCase> getRecentDocumentsProvider,
      Provider<GetFoldersUseCase> getFoldersProvider,
      Provider<GetStorageInfoUseCase> getStorageInfoProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<DeleteDocumentUseCase> deleteDocumentProvider,
      Provider<PreferencesDataStore> prefsProvider) {
    this.getRecentDocumentsProvider = getRecentDocumentsProvider;
    this.getFoldersProvider = getFoldersProvider;
    this.getStorageInfoProvider = getStorageInfoProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
    this.deleteDocumentProvider = deleteDocumentProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getRecentDocumentsProvider.get(), getFoldersProvider.get(), getStorageInfoProvider.get(), toggleFavoriteProvider.get(), deleteDocumentProvider.get(), prefsProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetRecentDocumentsUseCase> getRecentDocumentsProvider,
      Provider<GetFoldersUseCase> getFoldersProvider,
      Provider<GetStorageInfoUseCase> getStorageInfoProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<DeleteDocumentUseCase> deleteDocumentProvider,
      Provider<PreferencesDataStore> prefsProvider) {
    return new HomeViewModel_Factory(getRecentDocumentsProvider, getFoldersProvider, getStorageInfoProvider, toggleFavoriteProvider, deleteDocumentProvider, prefsProvider);
  }

  public static HomeViewModel newInstance(GetRecentDocumentsUseCase getRecentDocuments,
      GetFoldersUseCase getFolders, GetStorageInfoUseCase getStorageInfo,
      ToggleFavoriteUseCase toggleFavorite, DeleteDocumentUseCase deleteDocument,
      PreferencesDataStore prefs) {
    return new HomeViewModel(getRecentDocuments, getFolders, getStorageInfo, toggleFavorite, deleteDocument, prefs);
  }
}
