package com.paperflow.app.presentation.search;

import com.paperflow.app.domain.usecase.SearchDocumentsUseCase;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<SearchDocumentsUseCase> searchDocumentsProvider;

  public SearchViewModel_Factory(Provider<SearchDocumentsUseCase> searchDocumentsProvider) {
    this.searchDocumentsProvider = searchDocumentsProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(searchDocumentsProvider.get());
  }

  public static SearchViewModel_Factory create(
      Provider<SearchDocumentsUseCase> searchDocumentsProvider) {
    return new SearchViewModel_Factory(searchDocumentsProvider);
  }

  public static SearchViewModel newInstance(SearchDocumentsUseCase searchDocuments) {
    return new SearchViewModel(searchDocuments);
  }
}
