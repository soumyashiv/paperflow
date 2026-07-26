package com.paperflow.app.presentation.aichat;

import com.paperflow.app.data.local.datastore.PreferencesDataStore;
import com.paperflow.app.domain.repository.PageRepository;
import com.paperflow.app.domain.repository.SearchRepository;
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase;
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
public final class AIChatViewModel_Factory implements Factory<AIChatViewModel> {
  private final Provider<SearchRepository> searchRepoProvider;

  private final Provider<PageRepository> pageRepoProvider;

  private final Provider<GetDocumentByIdUseCase> getDocumentProvider;

  private final Provider<PreferencesDataStore> prefsProvider;

  public AIChatViewModel_Factory(Provider<SearchRepository> searchRepoProvider,
      Provider<PageRepository> pageRepoProvider,
      Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<PreferencesDataStore> prefsProvider) {
    this.searchRepoProvider = searchRepoProvider;
    this.pageRepoProvider = pageRepoProvider;
    this.getDocumentProvider = getDocumentProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public AIChatViewModel get() {
    return newInstance(searchRepoProvider.get(), pageRepoProvider.get(), getDocumentProvider.get(), prefsProvider.get());
  }

  public static AIChatViewModel_Factory create(Provider<SearchRepository> searchRepoProvider,
      Provider<PageRepository> pageRepoProvider,
      Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<PreferencesDataStore> prefsProvider) {
    return new AIChatViewModel_Factory(searchRepoProvider, pageRepoProvider, getDocumentProvider, prefsProvider);
  }

  public static AIChatViewModel newInstance(SearchRepository searchRepo, PageRepository pageRepo,
      GetDocumentByIdUseCase getDocument, PreferencesDataStore prefs) {
    return new AIChatViewModel(searchRepo, pageRepo, getDocument, prefs);
  }
}
