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
public final class GetAllDocumentsUseCase_Factory implements Factory<GetAllDocumentsUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public GetAllDocumentsUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetAllDocumentsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetAllDocumentsUseCase_Factory create(Provider<DocumentRepository> repoProvider) {
    return new GetAllDocumentsUseCase_Factory(repoProvider);
  }

  public static GetAllDocumentsUseCase newInstance(DocumentRepository repo) {
    return new GetAllDocumentsUseCase(repo);
  }
}
