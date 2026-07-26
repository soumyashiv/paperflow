package com.paperflow.app.data.repository;

import com.paperflow.app.data.local.database.dao.DocumentDao;
import com.paperflow.app.data.local.database.dao.FolderDao;
import com.paperflow.app.data.local.database.dao.NoteDao;
import com.paperflow.app.data.local.database.dao.OCRIndexDao;
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
public final class SearchRepositoryImpl_Factory implements Factory<SearchRepositoryImpl> {
  private final Provider<OCRIndexDao> ocrDaoProvider;

  private final Provider<DocumentDao> documentDaoProvider;

  private final Provider<NoteDao> noteDaoProvider;

  private final Provider<FolderDao> folderDaoProvider;

  public SearchRepositoryImpl_Factory(Provider<OCRIndexDao> ocrDaoProvider,
      Provider<DocumentDao> documentDaoProvider, Provider<NoteDao> noteDaoProvider,
      Provider<FolderDao> folderDaoProvider) {
    this.ocrDaoProvider = ocrDaoProvider;
    this.documentDaoProvider = documentDaoProvider;
    this.noteDaoProvider = noteDaoProvider;
    this.folderDaoProvider = folderDaoProvider;
  }

  @Override
  public SearchRepositoryImpl get() {
    return newInstance(ocrDaoProvider.get(), documentDaoProvider.get(), noteDaoProvider.get(), folderDaoProvider.get());
  }

  public static SearchRepositoryImpl_Factory create(Provider<OCRIndexDao> ocrDaoProvider,
      Provider<DocumentDao> documentDaoProvider, Provider<NoteDao> noteDaoProvider,
      Provider<FolderDao> folderDaoProvider) {
    return new SearchRepositoryImpl_Factory(ocrDaoProvider, documentDaoProvider, noteDaoProvider, folderDaoProvider);
  }

  public static SearchRepositoryImpl newInstance(OCRIndexDao ocrDao, DocumentDao documentDao,
      NoteDao noteDao, FolderDao folderDao) {
    return new SearchRepositoryImpl(ocrDao, documentDao, noteDao, folderDao);
  }
}
