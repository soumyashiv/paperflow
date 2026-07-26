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
public final class GetFoldersUseCase_Factory implements Factory<GetFoldersUseCase> {
  private final Provider<FolderRepository> repoProvider;

  public GetFoldersUseCase_Factory(Provider<FolderRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetFoldersUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetFoldersUseCase_Factory create(Provider<FolderRepository> repoProvider) {
    return new GetFoldersUseCase_Factory(repoProvider);
  }

  public static GetFoldersUseCase newInstance(FolderRepository repo) {
    return new GetFoldersUseCase(repo);
  }
}
