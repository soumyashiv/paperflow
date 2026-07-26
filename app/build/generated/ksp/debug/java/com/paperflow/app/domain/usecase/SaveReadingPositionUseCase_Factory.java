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
public final class SaveReadingPositionUseCase_Factory implements Factory<SaveReadingPositionUseCase> {
  private final Provider<DocumentRepository> repoProvider;

  public SaveReadingPositionUseCase_Factory(Provider<DocumentRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public SaveReadingPositionUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static SaveReadingPositionUseCase_Factory create(
      Provider<DocumentRepository> repoProvider) {
    return new SaveReadingPositionUseCase_Factory(repoProvider);
  }

  public static SaveReadingPositionUseCase newInstance(DocumentRepository repo) {
    return new SaveReadingPositionUseCase(repo);
  }
}
