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
public final class ScanProcessor_Factory implements Factory<ScanProcessor> {
  private final Provider<Context> contextProvider;

  private final Provider<FileStorage> storageProvider;

  public ScanProcessor_Factory(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    this.contextProvider = contextProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public ScanProcessor get() {
    return newInstance(contextProvider.get(), storageProvider.get());
  }

  public static ScanProcessor_Factory create(Provider<Context> contextProvider,
      Provider<FileStorage> storageProvider) {
    return new ScanProcessor_Factory(contextProvider, storageProvider);
  }

  public static ScanProcessor newInstance(Context context, FileStorage storage) {
    return new ScanProcessor(context, storage);
  }
}
