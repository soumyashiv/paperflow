package com.paperflow.app.presentation.workspace;

import com.paperflow.app.domain.usecase.CreateFolderUseCase;
import com.paperflow.app.domain.usecase.DeleteDocumentUseCase;
import com.paperflow.app.domain.usecase.GetAllDocumentsUseCase;
import com.paperflow.app.domain.usecase.GetDocumentsByTypeUseCase;
import com.paperflow.app.domain.usecase.GetFavoriteDocumentsUseCase;
import com.paperflow.app.domain.usecase.GetFolderContentsUseCase;
import com.paperflow.app.domain.usecase.GetFoldersUseCase;
import com.paperflow.app.domain.usecase.GetStorageInfoUseCase;
import com.paperflow.app.domain.usecase.ToggleFavoriteUseCase;
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
public final class WorkspaceViewModel_Factory implements Factory<WorkspaceViewModel> {
  private final Provider<GetAllDocumentsUseCase> getAllDocumentsProvider;

  private final Provider<GetFolderContentsUseCase> getFolderContentsProvider;

  private final Provider<GetFoldersUseCase> getFoldersProvider;

  private final Provider<ToggleFavoriteUseCase> toggleFavoriteProvider;

  private final Provider<DeleteDocumentUseCase> deleteDocumentProvider;

  private final Provider<CreateFolderUseCase> createFolderProvider;

  private final Provider<GetDocumentsByTypeUseCase> getDocumentsByTypeProvider;

  private final Provider<GetFavoriteDocumentsUseCase> getFavoriteDocumentsProvider;

  private final Provider<GetStorageInfoUseCase> getStorageInfoProvider;

  public WorkspaceViewModel_Factory(Provider<GetAllDocumentsUseCase> getAllDocumentsProvider,
      Provider<GetFolderContentsUseCase> getFolderContentsProvider,
      Provider<GetFoldersUseCase> getFoldersProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<DeleteDocumentUseCase> deleteDocumentProvider,
      Provider<CreateFolderUseCase> createFolderProvider,
      Provider<GetDocumentsByTypeUseCase> getDocumentsByTypeProvider,
      Provider<GetFavoriteDocumentsUseCase> getFavoriteDocumentsProvider,
      Provider<GetStorageInfoUseCase> getStorageInfoProvider) {
    this.getAllDocumentsProvider = getAllDocumentsProvider;
    this.getFolderContentsProvider = getFolderContentsProvider;
    this.getFoldersProvider = getFoldersProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
    this.deleteDocumentProvider = deleteDocumentProvider;
    this.createFolderProvider = createFolderProvider;
    this.getDocumentsByTypeProvider = getDocumentsByTypeProvider;
    this.getFavoriteDocumentsProvider = getFavoriteDocumentsProvider;
    this.getStorageInfoProvider = getStorageInfoProvider;
  }

  @Override
  public WorkspaceViewModel get() {
    return newInstance(getAllDocumentsProvider.get(), getFolderContentsProvider.get(), getFoldersProvider.get(), toggleFavoriteProvider.get(), deleteDocumentProvider.get(), createFolderProvider.get(), getDocumentsByTypeProvider.get(), getFavoriteDocumentsProvider.get(), getStorageInfoProvider.get());
  }

  public static WorkspaceViewModel_Factory create(
      Provider<GetAllDocumentsUseCase> getAllDocumentsProvider,
      Provider<GetFolderContentsUseCase> getFolderContentsProvider,
      Provider<GetFoldersUseCase> getFoldersProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<DeleteDocumentUseCase> deleteDocumentProvider,
      Provider<CreateFolderUseCase> createFolderProvider,
      Provider<GetDocumentsByTypeUseCase> getDocumentsByTypeProvider,
      Provider<GetFavoriteDocumentsUseCase> getFavoriteDocumentsProvider,
      Provider<GetStorageInfoUseCase> getStorageInfoProvider) {
    return new WorkspaceViewModel_Factory(getAllDocumentsProvider, getFolderContentsProvider, getFoldersProvider, toggleFavoriteProvider, deleteDocumentProvider, createFolderProvider, getDocumentsByTypeProvider, getFavoriteDocumentsProvider, getStorageInfoProvider);
  }

  public static WorkspaceViewModel newInstance(GetAllDocumentsUseCase getAllDocuments,
      GetFolderContentsUseCase getFolderContents, GetFoldersUseCase getFolders,
      ToggleFavoriteUseCase toggleFavorite, DeleteDocumentUseCase deleteDocument,
      CreateFolderUseCase createFolder, GetDocumentsByTypeUseCase getDocumentsByType,
      GetFavoriteDocumentsUseCase getFavoriteDocuments, GetStorageInfoUseCase getStorageInfo) {
    return new WorkspaceViewModel(getAllDocuments, getFolderContents, getFolders, toggleFavorite, deleteDocument, createFolder, getDocumentsByType, getFavoriteDocuments, getStorageInfo);
  }
}
