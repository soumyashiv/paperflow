package com.paperflow.app.data.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.paperflow.app.data.local.database.dao.DocumentDao;
import com.paperflow.app.data.local.database.dao.PageDao;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.PDFEngine;
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
public final class ThumbnailWorker_Factory {
  private final Provider<DocumentDao> documentDaoProvider;

  private final Provider<PageDao> pageDaoProvider;

  private final Provider<PDFEngine> pdfEngineProvider;

  private final Provider<FileStorage> fileStorageProvider;

  public ThumbnailWorker_Factory(Provider<DocumentDao> documentDaoProvider,
      Provider<PageDao> pageDaoProvider, Provider<PDFEngine> pdfEngineProvider,
      Provider<FileStorage> fileStorageProvider) {
    this.documentDaoProvider = documentDaoProvider;
    this.pageDaoProvider = pageDaoProvider;
    this.pdfEngineProvider = pdfEngineProvider;
    this.fileStorageProvider = fileStorageProvider;
  }

  public ThumbnailWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, documentDaoProvider.get(), pageDaoProvider.get(), pdfEngineProvider.get(), fileStorageProvider.get());
  }

  public static ThumbnailWorker_Factory create(Provider<DocumentDao> documentDaoProvider,
      Provider<PageDao> pageDaoProvider, Provider<PDFEngine> pdfEngineProvider,
      Provider<FileStorage> fileStorageProvider) {
    return new ThumbnailWorker_Factory(documentDaoProvider, pageDaoProvider, pdfEngineProvider, fileStorageProvider);
  }

  public static ThumbnailWorker newInstance(Context context, WorkerParameters workerParams,
      DocumentDao documentDao, PageDao pageDao, PDFEngine pdfEngine, FileStorage fileStorage) {
    return new ThumbnailWorker(context, workerParams, documentDao, pageDao, pdfEngine, fileStorage);
  }
}
