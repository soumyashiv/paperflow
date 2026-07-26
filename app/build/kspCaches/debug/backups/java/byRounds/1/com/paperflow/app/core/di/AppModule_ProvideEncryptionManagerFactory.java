package com.paperflow.app.core.di;

import android.content.Context;
import com.paperflow.app.data.local.file.EncryptionManager;
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
public final class AppModule_ProvideEncryptionManagerFactory implements Factory<EncryptionManager> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideEncryptionManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public EncryptionManager get() {
    return provideEncryptionManager(contextProvider.get());
  }

  public static AppModule_ProvideEncryptionManagerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideEncryptionManagerFactory(contextProvider);
  }

  public static EncryptionManager provideEncryptionManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideEncryptionManager(context));
  }
}
