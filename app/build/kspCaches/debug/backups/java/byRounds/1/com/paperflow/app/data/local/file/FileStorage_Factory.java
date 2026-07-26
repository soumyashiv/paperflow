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
public final class FileStorage_Factory implements Factory<FileStorage> {
  private final Provider<Context> contextProvider;

  public FileStorage_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public FileStorage get() {
    return newInstance(contextProvider.get());
  }

  public static FileStorage_Factory create(Provider<Context> contextProvider) {
    return new FileStorage_Factory(contextProvider);
  }

  public static FileStorage newInstance(Context context) {
    return new FileStorage(context);
  }
}
