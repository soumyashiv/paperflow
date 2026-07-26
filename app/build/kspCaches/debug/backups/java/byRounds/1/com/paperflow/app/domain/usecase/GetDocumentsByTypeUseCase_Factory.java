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
public final class GetDocumentsByTypeUseCase_Factory implements Factory<GetDocumentsByTypeUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public GetDocumentsByTypeUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetDocumentsByTypeUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetDocumentsByTypeUseCase_Factory create(
      Provider<DocumentRepository> repoProvider) {
    return new GetDocumentsByTypeUseCase_Factory(repoProvider);
  }

  public static GetDocumentsByTypeUseCase newInstance(DocumentRepository repo) {
    return new GetDocumentsByTypeUseCase(repo);
  }
}
