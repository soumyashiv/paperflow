package com.paperflow.app.data.repository;

import com.paperflow.app.data.local.database.dao.DocumentDao;
import com.paperflow.app.data.local.database.dao.FolderDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class FolderRepositoryImpl_Factory implements Factory<FolderRepositoryImpl> {
  private final Provider<FolderDao> folderDaoProvider;

  private final Provider<DocumentDao> documentDaoProvider;

  public FolderRepositoryImpl_Factory(Provider<FolderDao> folderDaoProvider,
      Provider<DocumentDao> documentDaoProvider) {
    this.folderDaoProvider = folderDaoProvider;
    this.documentDaoProvider = documentDaoProvider;
  }

  @Override
  public FolderRepositoryImpl get() {
    return newInstance(folderDaoProvider.get(), documentDaoProvider.get());
  }

  public static FolderRepositoryImpl_Factory create(Provider<FolderDao> folderDaoProvider,
      Provider<DocumentDao> documentDaoProvider) {
    return new FolderRepositoryImpl_Factory(folderDaoProvider, documentDaoProvider);
  }

  public static FolderRepositoryImpl newInstance(FolderDao folderDao, DocumentDao documentDao) {
    return new FolderRepositoryImpl(folderDao, documentDao);
  }
}
