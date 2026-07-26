package com.paperflow.app.data.workers;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = ThumbnailWorker.class
)
public interface ThumbnailWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.paperflow.app.data.workers.ThumbnailWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(ThumbnailWorker_AssistedFactory factory);
}
