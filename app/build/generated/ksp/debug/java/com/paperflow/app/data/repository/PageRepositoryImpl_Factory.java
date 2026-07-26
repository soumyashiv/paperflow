package com.paperflow.app.data.repository;

import com.paperflow.app.data.local.database.dao.PageDao;
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
public final class PageRepositoryImpl_Factory implements Factory<PageRepositoryImpl> {
  private final Provider<PageDao> daoProvider;

  public PageRepositoryImpl_Factory(Provider<PageDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public PageRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static PageRepositoryImpl_Factory create(Provider<PageDao> daoProvider) {
    return new PageRepositoryImpl_Factory(daoProvider);
  }

  public static PageRepositoryImpl newInstance(PageDao dao) {
    return new PageRepositoryImpl(dao);
  }
}
