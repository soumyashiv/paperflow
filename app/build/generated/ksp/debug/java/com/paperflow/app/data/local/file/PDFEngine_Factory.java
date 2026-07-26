package com.paperflow.app.data.local.file;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PDFEngine_Factory implements Factory<PDFEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<FileStorage> storageProvider;

  public PDFEngine_Factory(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    this.contextProvider = contextProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public PDFEngine get() {
    return newInstance(contextProvider.get(), storageProvider.get());
  }

  public static PDFEngine_Factory create(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    return new PDFEngine_Factory(contextProvider, storageProvider);
  }

  public static PDFEngine newInstance(Context context, FileStorage storage) {
    return new PDFEngine(context, storage);
  }
}
