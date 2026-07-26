package com.paperflow.app.core.di;

import com.paperflow.app.data.local.database.AppDatabase;
import com.paperflow.app.data.local.database.dao.OCRIndexDao;
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
public final class DatabaseModule_ProvideOCRIndexDaoFactory implements Factory<OCRIndexDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideOCRIndexDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public OCRIndexDao get() {
    return provideOCRIndexDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideOCRIndexDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideOCRIndexDaoFactory(dbProvider);
  }

  public static OCRIndexDao provideOCRIndexDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideOCRIndexDao(db));
  }
}
