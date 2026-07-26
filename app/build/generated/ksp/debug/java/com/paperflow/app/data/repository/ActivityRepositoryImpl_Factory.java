package com.paperflow.app.data.repository;

import com.paperflow.app.data.local.database.dao.ActivityDao;
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
public final class ActivityRepositoryImpl_Factory implements Factory<ActivityRepositoryImpl> {
  private final Provider<ActivityDao> daoProvider;

  public ActivityRepositoryImpl_Factory(Provider<ActivityDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ActivityRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ActivityRepositoryImpl_Factory create(Provider<ActivityDao> daoProvider) {
    return new ActivityRepositoryImpl_Factory(daoProvider);
  }

  public static ActivityRepositoryImpl newInstance(ActivityDao dao) {
    return new ActivityRepositoryImpl(dao);
  }
}
