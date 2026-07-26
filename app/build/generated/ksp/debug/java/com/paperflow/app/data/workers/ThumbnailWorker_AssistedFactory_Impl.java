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
public final class ThumbnailWorker_AssistedFactory_Impl implements ThumbnailWorker_AssistedFactory {
  private final ThumbnailWorker_Factory delegateFactory;

  ThumbnailWorker_AssistedFactory_Impl(ThumbnailWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ThumbnailWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ThumbnailWorker_AssistedFactory> create(
      ThumbnailWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ThumbnailWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ThumbnailWorker_AssistedFactory> createFactoryProvider(
      ThumbnailWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ThumbnailWorker_AssistedFactory_Impl(delegateFactory));
  }
}
