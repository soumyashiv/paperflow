package com.paperflow.app.presentation.vault;

import android.content.Context;
import com.paperflow.app.data.local.datastore.PreferencesDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class VaultViewModel_Factory implements Factory<VaultViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<PreferencesDataStore> prefsProvider;

  public VaultViewModel_Factory(Provider<Context> contextProvider,
      Provider<PreferencesDataStore> prefsProvider) {
    this.contextProvider = contextProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public VaultViewModel get() {
    return newInstance(contextProvider.get(), prefsProvider.get());
  }

  public static VaultViewModel_Factory create(Provider<Context> contextProvider,
      Provider<PreferencesDataStore> prefsProvider) {
    return new VaultViewModel_Factory(contextProvider, prefsProvider);
  }

  public static VaultViewModel newInstance(Context context, PreferencesDataStore prefs) {
    return new VaultViewModel(context, prefs);
  }
}
