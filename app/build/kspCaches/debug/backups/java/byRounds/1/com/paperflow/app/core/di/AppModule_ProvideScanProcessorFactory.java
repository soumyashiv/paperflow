package com.paperflow.app.core.di;

import android.content.Context;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.ScanProcessor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideScanProcessorFactory implements Factory<ScanProcessor> {
  private final Provider<Context> contextProvider;

  private final Provider<FileStorage> storageProvider;

  public AppModule_ProvideScanProcessorFactory(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    this.contextProvider = contextProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public ScanProcessor get() {
    return provideScanProcessor(contextProvider.get(), storageProvider.get());
  }

  public static AppModule_ProvideScanProcessorFactory create(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    return new AppModule_ProvideScanProcessorFactory(contextProvider, storageProvider);
  }

  public static ScanProcessor provideScanProcessor(Context context, FileStorage storage) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideScanProcessor(context, storage));
  }
}
