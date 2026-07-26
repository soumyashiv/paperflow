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
public final class GetDocumentByIdUseCase_Factory implements Factory<GetDocumentByIdUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public GetDocumentByIdUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetDocumentByIdUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetDocumentByIdUseCase_Factory create(Provider<DocumentRepository> repoProvider) {
    return new GetDocumentByIdUseCase_Factory(repoProvider);
  }

  public static GetDocumentByIdUseCase newInstance(DocumentRepository repo) {
    return new GetDocumentByIdUseCase(repo);
  }
}
