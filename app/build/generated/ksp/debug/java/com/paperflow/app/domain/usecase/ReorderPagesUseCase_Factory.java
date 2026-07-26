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
public final class ReorderPagesUseCase_Factory implements Factory<ReorderPagesUseCase> {
  private final Provider<Context> contextProvider;

  private final Provider<PageRepository> pageRepoProvider;

  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<PDFEngine> pdfEngineProvider;

  private final Provider<FileStorage> storageProvider;

  public ReorderPagesUseCase_Factory(Provider<Context> contextProvider,
      Provider<PageRepository> pageRepoProvider, Provider<DocumentRepository> documentRepoProvider,
      Provider<PDFEngine> pdfEngineProvider, Provider<FileStorage> storageProvider) {
    this.contextProvider = contextProvider;
    this.pageRepoProvider = pageRepoProvider;
    this.documentRepoProvider = documentRepoProvider;
    this.pdfEngineProvider = pdfEngineProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public ReorderPagesUseCase get() {
    return newInstance(contextProvider.get(), pageRepoProvider.get(), documentRepoProvider.get(), pdfEngineProvider.get(), storageProvider.get());
  }

  public static ReorderPagesUseCase_Factory create(Provider<Context> contextProvider,
      Provider<PageRepository> pageRepoProvider, Provider<DocumentRepository> documentRepoProvider,
      Provider<PDFEngine> pdfEngineProvider, Provider<FileStorage> storageProvider) {
    return new ReorderPagesUseCase_Factory(contextProvider, pageRepoProvider, documentRepoProvider, pdfEngineProvider, storageProvider);
  }

  public static ReorderPagesUseCase newInstance(Context context, PageRepository pageRepo,
      DocumentRepository documentRepo, PDFEngine pdfEngine, FileStorage storage) {
    return new ReorderPagesUseCase(context, pageRepo, documentRepo, pdfEngine, storage);
  }
}
