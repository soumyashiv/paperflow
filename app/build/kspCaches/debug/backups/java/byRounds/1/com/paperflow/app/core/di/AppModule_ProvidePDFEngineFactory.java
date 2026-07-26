package com.paperflow.app.core.di;

import android.content.Context;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.PDFEngine;
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
public final class AppModule_ProvidePDFEngineFactory implements Factory<PDFEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<FileStorage> storageProvider;

  public AppModule_ProvidePDFEngineFactory(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    this.contextProvider = contextProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public PDFEngine get() {
    return providePDFEngine(contextProvider.get(), storageProvider.get());
  }

  public static AppModule_ProvidePDFEngineFactory create(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    return new AppModule_ProvidePDFEngineFactory(contextProvider, storageProvider);
  }

  public static PDFEngine providePDFEngine(Context context, FileStorage storage) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePDFEngine(context, storage));
  }
}
