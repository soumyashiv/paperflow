package com.paperflow.app.core.di;

import com.paperflow.app.data.local.database.AppDatabase;
import com.paperflow.app.data.local.database.dao.AnnotationDao;
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
public final class DatabaseModule_ProvideAnnotationDaoFactory implements Factory<AnnotationDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideAnnotationDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AnnotationDao get() {
    return provideAnnotationDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAnnotationDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideAnnotationDaoFactory(dbProvider);
  }

  public static AnnotationDao provideAnnotationDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAnnotationDao(db));
  }
}
