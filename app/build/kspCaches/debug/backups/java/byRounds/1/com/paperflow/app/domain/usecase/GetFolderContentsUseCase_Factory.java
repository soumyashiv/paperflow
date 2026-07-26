package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.DocumentRepository;
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
public final class GetFolderContentsUseCase_Factory implements Factory<GetFolderContentsUseCase> {
  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<FolderRepository> folderRepoProvider;

  public GetFolderContentsUseCase_Factory(Provider<DocumentRepository> documentRepoProvider,
      Provider<FolderRepository> folderRepoProvider) {
    this.documentRepoProvider = documentRepoProvider;
    this.folderRepoProvider = folderRepoProvider;
  }

  @Override
  public GetFolderContentsUseCase get() {
    return newInstance(documentRepoProvider.get(), folderRepoProvider.get());
  }

  public static GetFolderContentsUseCase_Factory create(
      Provider<DocumentRepository> documentRepoProvider,
      Provider<FolderRepository> folderRepoProvider) {
    return new GetFolderContentsUseCase_Factory(documentRepoProvider, folderRepoProvider);
  }

  public static GetFolderContentsUseCase newInstance(DocumentRepository documentRepo,
      FolderRepository folderRepo) {
    return new GetFolderContentsUseCase(documentRepo, folderRepo);
  }
}
