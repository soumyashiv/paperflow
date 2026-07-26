package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.AnnotationRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SaveAnnotationUseCase_Factory implements Factory<SaveAnnotationUseCase> {
  private final Provider<AnnotationRepository> repoProvider;

  public SaveAnnotationUseCase_Factory(Provider<AnnotationRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public SaveAnnotationUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static SaveAnnotationUseCase_Factory create(Provider<AnnotationRepository> repoProvider) {
    return new SaveAnnotationUseCase_Factory(repoProvider);
  }

  public static SaveAnnotationUseCase newInstance(AnnotationRepository repo) {
    return new SaveAnnotationUseCase(repo);
  }
}
