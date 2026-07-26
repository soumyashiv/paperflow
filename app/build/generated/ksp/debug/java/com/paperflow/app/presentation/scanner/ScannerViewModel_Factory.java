package com.paperflow.app.presentation.scanner;

import android.content.Context;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.ScanProcessor;
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
public final class ScannerViewModel_Factory implements Factory<ScannerViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<FileStorage> storageProvider;

  private final Provider<ScanProcessor> scanProcessorProvider;

  public ScannerViewModel_Factory(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider, Provider<ScanProcessor> scanProcessorProvider) {
    this.contextProvider = contextProvider;
    this.storageProvider = storageProvider;
    this.scanProcessorProvider = scanProcessorProvider;
  }

  @Override
  public ScannerViewModel get() {
    return newInstance(contextProvider.get(), storageProvider.get(), scanProcessorProvider.get());
  }

  public static ScannerViewModel_Factory create(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider, Provider<ScanProcessor> scanProcessorProvider) {
    return new ScannerViewModel_Factory(contextProvider, storageProvider, scanProcessorProvider);
  }

  public static ScannerViewModel newInstance(Context context, FileStorage storage,
      ScanProcessor scanProcessor) {
    return new ScannerViewModel(context, storage, scanProcessor);
  }
}
