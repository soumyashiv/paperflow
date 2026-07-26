package com.paperflow.app.presentation.upload;

import com.paperflow.app.domain.usecase.ImportDocumentUseCase;
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
public final class UploadViewModel_Factory implements Factory<UploadViewModel> {
  private final Provider<ImportDocumentUseCase> importDocumentProvider;

  public UploadViewModel_Factory(Provider<ImportDocumentUseCase> importDocumentProvider) {
    this.importDocumentProvider = importDocumentProvider;
  }

  @Override
  public UploadViewModel get() {
    return newInstance(importDocumentProvider.get());
  }

  public static UploadViewModel_Factory create(
      Provider<ImportDocumentUseCase> importDocumentProvider) {
    return new UploadViewModel_Factory(importDocumentProvider);
  }

  public static UploadViewModel newInstance(ImportDocumentUseCase importDocument) {
    return new UploadViewModel(importDocument);
  }
}
