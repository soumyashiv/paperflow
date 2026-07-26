package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.SearchRepository;
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
public final class SearchDocumentsUseCase_Factory implements Factory<SearchDocumentsUseCase> {
  private final Provider<SearchRepository> repoProvider;

  public SearchDocumentsUseCase_Factory(Provider<SearchRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public SearchDocumentsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static SearchDocumentsUseCase_Factory create(Provider<SearchRepository> repoProvider) {
    return new SearchDocumentsUseCase_Factory(repoProvider);
  }

  public static SearchDocumentsUseCase newInstance(SearchRepository repo) {
    return new SearchDocumentsUseCase(repo);
  }
}
