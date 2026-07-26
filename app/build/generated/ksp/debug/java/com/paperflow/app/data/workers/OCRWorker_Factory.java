package com.paperflow.app.data.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.paperflow.app.data.local.database.dao.DocumentDao;
import com.paperflow.app.data.local.database.dao.PageDao;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.PDFEngine;
import com.paperflow.app.domain.repository.OCRRepository;
import dagger.internal.DaggerGenerated;
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
public final class OCRWorker_Factory {
  private final Provider<DocumentDao> documentDaoProvider;

  private final Provider<PageDao> pageDaoProvider;

  private final Provider<OCRRepository> ocrRepositoryProvider;

  private final Provider<FileStorage> fileStorageProvider;

  private final Provider<PDFEngine> pdfEngineProvider;

  public OCRWorker_Factory(Provider<DocumentDao> documentDaoProvider,
      Provider<PageDao> pageDaoProvider, Provider<OCRRepository> ocrRepositoryProvider,
      Provider<FileStorage> fileStorageProvider, Provider<PDFEngine> pdfEngineProvider) {
    this.documentDaoProvider = documentDaoProvider;
    this.pageDaoProvider = pageDaoProvider;
    this.ocrRepositoryProvider = ocrRepositoryProvider;
    this.fileStorageProvider = fileStorageProvider;
    this.pdfEngineProvider = pdfEngineProvider;
  }

  public OCRWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, documentDaoProvider.get(), pageDaoProvider.get(), ocrRepositoryProvider.get(), fileStorageProvider.get(), pdfEngineProvider.get());
  }

  public static OCRWorker_Factory create(Provider<DocumentDao> documentDaoProvider,
      Provider<PageDao> pageDaoProvider, Provider<OCRRepository> ocrRepositoryProvider,
      Provider<FileStorage> fileStorageProvider, Provider<PDFEngine> pdfEngineProvider) {
    return new OCRWorker_Factory(documentDaoProvider, pageDaoProvider, ocrRepositoryProvider, fileStorageProvider, pdfEngineProvider);
  }

  public static OCRWorker newInstance(Context context, WorkerParameters workerParams,
      DocumentDao documentDao, PageDao pageDao, OCRRepository ocrRepository,
      FileStorage fileStorage, PDFEngine pdfEngine) {
    return new OCRWorker(context, workerParams, documentDao, pageDao, ocrRepository, fileStorage, pdfEngine);
  }
}
