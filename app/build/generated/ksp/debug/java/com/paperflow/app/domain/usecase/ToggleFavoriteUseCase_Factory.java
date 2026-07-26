package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.DocumentRepository;
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
public final class ToggleFavoriteUseCase_Factory implements Factory<ToggleFavoriteUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public ToggleFavoriteUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public ToggleFavoriteUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static ToggleFavoriteUseCase_Factory create(Provider<DocumentRepository> repoProvider) {
    return new ToggleFavoriteUseCase_Factory(repoProvider);
  }

  public static ToggleFavoriteUseCase newInstance(DocumentRepository repo) {
    return new ToggleFavoriteUseCase(repo);
  }
}
