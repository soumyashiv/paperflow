package com.paperflow.app.domain.usecase;

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
public final class GetRecentDocumentsUseCase_Factory implements Factory<GetRecentDocumentsUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public GetRecentDocumentsUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetRecentDocumentsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetRecentDocumentsUseCase_Factory create(
      Provider<DocumentRepository> repoProvider) {
    return new GetRecentDocumentsUseCase_Factory(repoProvider);
  }

  public static GetRecentDocumentsUseCase newInstance(DocumentRepository repo) {
    return new GetRecentDocumentsUseCase(repo);
  }
}
