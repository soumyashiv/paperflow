package com.paperflow.app.domain.usecase;

import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.domain.repository.DocumentRepository;
import com.paperflow.app.domain.repository.OCRRepository;
import com.paperflow.app.domain.repository.PageRepository;
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
public final class DeleteDocumentUseCase_Factory implements Factory<DeleteDocumentUseCase> {
  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<PageRepository> pageRepoProvider;

  private final Provider<OCRRepository> ocrRepoProvider;

  private final Provider<FileStorage> storageProvider;

  public DeleteDocumentUseCase_Factory(Provider<DocumentRepository> documentRepoProvider,
      Provider<PageRepository> pageRepoProvider, Provider<OCRRepository> ocrRepoProvider,
      Provider<FileStorage> storageProvider) {
    this.documentRepoProvider = documentRepoProvider;
    this.pageRepoProvider = pageRepoProvider;
    this.ocrRepoProvider = ocrRepoProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public DeleteDocumentUseCase get() {
    return newInstance(documentRepoProvider.get(), pageRepoProvider.get(), ocrRepoProvider.get(), storageProvider.get());
  }

  public static DeleteDocumentUseCase_Factory create(
      Provider<DocumentRepository> documentRepoProvider, Provider<PageRepository> pageRepoProvider,
      Provider<OCRRepository> ocrRepoProvider, Provider<FileStorage> storageProvider) {
    return new DeleteDocumentUseCase_Factory(documentRepoProvider, pageRepoProvider, ocrRepoProvider, storageProvider);
  }

  public static DeleteDocumentUseCase newInstance(DocumentRepository documentRepo,
      PageRepository pageRepo, OCRRepository ocrRepo, FileStorage storage) {
    return new DeleteDocumentUseCase(documentRepo, pageRepo, ocrRepo, storage);
  }
}
