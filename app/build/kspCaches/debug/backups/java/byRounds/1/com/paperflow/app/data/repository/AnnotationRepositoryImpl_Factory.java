package com.paperflow.app.data.repository;

import com.paperflow.app.data.local.database.dao.AnnotationDao;
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
public final class AnnotationRepositoryImpl_Factory implements Factory<AnnotationRepositoryImpl> {
  private final Provider<AnnotationDao> daoProvider;

  public AnnotationRepositoryImpl_Factory(Provider<AnnotationDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public AnnotationRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static AnnotationRepositoryImpl_Factory create(Provider<AnnotationDao> daoProvider) {
    return new AnnotationRepositoryImpl_Factory(daoProvider);
  }

  public static AnnotationRepositoryImpl newInstance(AnnotationDao dao) {
    return new AnnotationRepositoryImpl(dao);
  }
}
