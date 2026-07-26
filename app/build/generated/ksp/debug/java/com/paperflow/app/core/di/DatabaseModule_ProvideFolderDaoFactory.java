package com.paperflow.app.core.di;

import com.paperflow.app.data.local.database.AppDatabase;
import com.paperflow.app.data.local.database.dao.FolderDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideFolderDaoFactory implements Factory<FolderDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideFolderDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FolderDao get() {
    return provideFolderDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFolderDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideFolderDaoFactory(dbProvider);
  }

  public static FolderDao provideFolderDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFolderDao(db));
  }
}
