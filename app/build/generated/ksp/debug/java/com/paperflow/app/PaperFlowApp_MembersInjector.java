package com.paperflow.app;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
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
public final class PaperFlowApp_MembersInjector implements MembersInjector<PaperFlowApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public PaperFlowApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<PaperFlowApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new PaperFlowApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(PaperFlowApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.paperflow.app.PaperFlowApp.workerFactory")
  public static void injectWorkerFactory(PaperFlowApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
