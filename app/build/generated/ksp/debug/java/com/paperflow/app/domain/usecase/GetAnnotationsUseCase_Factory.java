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
public final class GetAnnotationsUseCase_Factory implements Factory<GetAnnotationsUseCase> {
  private final Provider<AnnotationRepository> repoProvider;

  public GetAnnotationsUseCase_Factory(Provider<AnnotationRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetAnnotationsUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetAnnotationsUseCase_Factory create(Provider<AnnotationRepository> repoProvider) {
    return new GetAnnotationsUseCase_Factory(repoProvider);
  }

  public static GetAnnotationsUseCase newInstance(AnnotationRepository repo) {
    return new GetAnnotationsUseCase(repo);
  }
}
