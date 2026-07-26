package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.FolderRepository;
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
public final class CreateFolderUseCase_Factory implements Factory<CreateFolderUseCase> {
  private final Provider<FolderRepository> repoProvider;

  public CreateFolderUseCase_Factory(Provider<FolderRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public CreateFolderUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static CreateFolderUseCase_Factory create(Provider<FolderRepository> repoProvider) {
    return new CreateFolderUseCase_Factory(repoProvider);
  }

  public static CreateFolderUseCase newInstance(FolderRepository repo) {
    return new CreateFolderUseCase(repo);
  }
}
