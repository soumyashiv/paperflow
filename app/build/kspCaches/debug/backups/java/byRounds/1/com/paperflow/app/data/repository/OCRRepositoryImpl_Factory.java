package com.paperflow.app.data.repository;

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
public final class OCRRepositoryImpl_Factory implements Factory<OCRRepositoryImpl> {
  private final Provider<OCRIndexDao> daoProvider;

  public OCRRepositoryImpl_Factory(Provider<OCRIndexDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public OCRRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static OCRRepositoryImpl_Factory create(Provider<OCRIndexDao> daoProvider) {
    return new OCRRepositoryImpl_Factory(daoProvider);
  }

  public static OCRRepositoryImpl newInstance(OCRIndexDao dao) {
    return new OCRRepositoryImpl(dao);
  }
}
