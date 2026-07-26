package com.paperflow.app.domain.usecase;

import android.content.Context;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.PDFEngine;
import com.paperflow.app.domain.repository.DocumentRepository;
import com.paperflow.app.domain.repository.PageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ImportDocumentUseCase_Factory implements Factory<ImportDocumentUseCase> {
  private final Provider<Context> contextProvider;

  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<PageRepository> pageRepoProvider;

  private final Provider<FileStorage> storageProvider;

  private final Provider<PDFEngine> pdfEngineProvider;

  public ImportDocumentUseCase_Factory(Provider<Context> contextProvider,
      Provider<DocumentRepository> documentRepoProvider, Provider<PageRepository> pageRepoProvider,
      Provider<FileStorage> storageProvider, Provider<PDFEngine> pdfEngineProvider) {
    this.contextProvider = contextProvider;
    this.documentRepoProvider = documentRepoProvider;
    this.pageRepoProvider = pageRepoProvider;
    this.storageProvider = storageProvider;
    this.pdfEngineProvider = pdfEngineProvider;
  }

  @Override
  public ImportDocumentUseCase get() {
    return newInstance(contextProvider.get(), documentRepoProvider.get(), pageRepoProvider.get(), storageProvider.get(), pdfEngineProvider.get());
  }

  public static ImportDocumentUseCase_Factory create(Provider<Context> contextProvider,
      Provider<DocumentRepository> documentRepoProvider, Provider<PageRepository> pageRepoProvider,
      Provider<FileStorage> storageProvider, Provider<PDFEngine> pdfEngineProvider) {
    return new ImportDocumentUseCase_Factory(contextProvider, documentRepoProvider, pageRepoProvider, storageProvider, pdfEngineProvider);
  }

  public static ImportDocumentUseCase newInstance(Context context, DocumentRepository documentRepo,
      PageRepository pageRepo, FileStorage storage, PDFEngine pdfEngine) {
    return new ImportDocumentUseCase(context, documentRepo, pageRepo, storage, pdfEngine);
  }
}
