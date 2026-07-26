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
public final class GetFavoriteDocumentsUseCase_Factory implements Factory<GetFavoriteDocumentsUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public GetFavoriteDocumentsUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetFavoriteDocumentsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetFavoriteDocumentsUseCase_Factory create(
      Provider<DocumentRepository> repoProvider) {
    return new GetFavoriteDocumentsUseCase_Factory(repoProvider);
  }

  public static GetFavoriteDocumentsUseCase newInstance(DocumentRepository repo) {
    return new GetFavoriteDocumentsUseCase(repo);
  }
}
