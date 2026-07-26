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
    topLevelClass = OCRWorker.class
)
public interface OCRWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.paperflow.app.data.workers.OCRWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(OCRWorker_AssistedFactory factory);
}
