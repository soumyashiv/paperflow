package com.paperflow.app.presentation.scanner;

import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.ScanProcessor;
import com.paperflow.app.domain.usecase.SaveScanAsDocumentUseCase;
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
public final class EditScanViewModel_Factory implements Factory<EditScanViewModel> {
  private final Provider<FileStorage> storageProvider;

  private final Provider<ScanProcessor> scanProcessorProvider;

  private final Provider<SaveScanAsDocumentUseCase> saveScanProvider;

  public EditScanViewModel_Factory(Provider<FileStorage> storageProvider,
      Provider<ScanProcessor> scanProcessorProvider,
      Provider<SaveScanAsDocumentUseCase> saveScanProvider) {
    this.storageProvider = storageProvider;
    this.scanProcessorProvider = scanProcessorProvider;
    this.saveScanProvider = saveScanProvider;
  }

  @Override
  public EditScanViewModel get() {
    return newInstance(storageProvider.get(), scanProcessorProvider.get(), saveScanProvider.get());
  }

  public static EditScanViewModel_Factory create(Provider<FileStorage> storageProvider,
      Provider<ScanProcessor> scanProcessorProvider,
      Provider<SaveScanAsDocumentUseCase> saveScanProvider) {
    return new EditScanViewModel_Factory(storageProvider, scanProcessorProvider, saveScanProvider);
  }

  public static EditScanViewModel newInstance(FileStorage storage, ScanProcessor scanProcessor,
      SaveScanAsDocumentUseCase saveScan) {
    return new EditScanViewModel(storage, scanProcessor, saveScan);
  }
}
