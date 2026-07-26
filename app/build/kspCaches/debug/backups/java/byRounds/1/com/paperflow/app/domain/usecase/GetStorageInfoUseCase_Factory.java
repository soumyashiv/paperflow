package com.paperflow.app.domain.usecase;

import com.paperflow.app.data.local.file.FileStorage;
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
public final class GetStorageInfoUseCase_Factory implements Factory<GetStorageInfoUseCase> {
  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<FileStorage> storageProvider;

  public GetStorageInfoUseCase_Factory(Provider<DocumentRepository> documentRepoProvider,
      Provider<FileStorage> storageProvider) {
    this.documentRepoProvider = documentRepoProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public GetStorageInfoUseCase get() {
    return newInstance(documentRepoProvider.get(), storageProvider.get());
  }

  public static GetStorageInfoUseCase_Factory create(
      Provider<DocumentRepository> documentRepoProvider, Provider<FileStorage> storageProvider) {
    return new GetStorageInfoUseCase_Factory(documentRepoProvider, storageProvider);
  }

  public static GetStorageInfoUseCase newInstance(DocumentRepository documentRepo,
      FileStorage storage) {
    return new GetStorageInfoUseCase(documentRepo, storage);
  }
}
