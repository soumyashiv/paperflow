package com.paperflow.app.data.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class OCRWorker_AssistedFactory_Impl implements OCRWorker_AssistedFactory {
  private final OCRWorker_Factory delegateFactory;

  OCRWorker_AssistedFactory_Impl(OCRWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public OCRWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<OCRWorker_AssistedFactory> create(OCRWorker_Factory delegateFactory) {
    return InstanceFactory.create(new OCRWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<OCRWorker_AssistedFactory> createFactoryProvider(
      OCRWorker_Factory delegateFactory) {
    return InstanceFactory.create(new OCRWorker_AssistedFactory_Impl(delegateFactory));
  }
}
