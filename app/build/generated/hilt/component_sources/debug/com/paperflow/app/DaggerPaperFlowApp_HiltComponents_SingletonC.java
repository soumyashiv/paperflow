package com.paperflow.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.paperflow.app.core.di.AppModule_ProvideFileStorageFactory;
import com.paperflow.app.core.di.AppModule_ProvidePDFEngineFactory;
import com.paperflow.app.core.di.AppModule_ProvideScanProcessorFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideActivityDaoFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideAnnotationDaoFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideDatabaseFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideDocumentDaoFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideFolderDaoFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideNoteDaoFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvideOCRIndexDaoFactory;
import com.paperflow.app.core.di.DatabaseModule_ProvidePageDaoFactory;
import com.paperflow.app.data.local.database.AppDatabase;
import com.paperflow.app.data.local.database.dao.ActivityDao;
import com.paperflow.app.data.local.database.dao.AnnotationDao;
import com.paperflow.app.data.local.database.dao.DocumentDao;
import com.paperflow.app.data.local.database.dao.FolderDao;
import com.paperflow.app.data.local.database.dao.NoteDao;
import com.paperflow.app.data.local.database.dao.OCRIndexDao;
import com.paperflow.app.data.local.database.dao.PageDao;
import com.paperflow.app.data.local.datastore.PreferencesDataStore;
import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.PDFEngine;
import com.paperflow.app.data.local.file.ScanProcessor;
import com.paperflow.app.data.repository.ActivityRepositoryImpl;
import com.paperflow.app.data.repository.AnnotationRepositoryImpl;
import com.paperflow.app.data.repository.DocumentRepositoryImpl;
import com.paperflow.app.data.repository.FolderRepositoryImpl;
import com.paperflow.app.data.repository.NoteRepositoryImpl;
import com.paperflow.app.data.repository.OCRRepositoryImpl;
import com.paperflow.app.data.repository.PageRepositoryImpl;
import com.paperflow.app.data.repository.SearchRepositoryImpl;
import com.paperflow.app.data.workers.OCRWorker;
import com.paperflow.app.data.workers.OCRWorker_AssistedFactory;
import com.paperflow.app.data.workers.ThumbnailWorker;
import com.paperflow.app.data.workers.ThumbnailWorker_AssistedFactory;
import com.paperflow.app.domain.usecase.AutoSaveNoteUseCase;
import com.paperflow.app.domain.usecase.ConvertScanToNoteUseCase;
import com.paperflow.app.domain.usecase.CreateFolderUseCase;
import com.paperflow.app.domain.usecase.CreateNoteUseCase;
import com.paperflow.app.domain.usecase.DeleteDocumentUseCase;
import com.paperflow.app.domain.usecase.DeleteNoteUseCase;
import com.paperflow.app.domain.usecase.GetAllDocumentsUseCase;
import com.paperflow.app.domain.usecase.GetAllNotesUseCase;
import com.paperflow.app.domain.usecase.GetAnnotationsUseCase;
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase;
import com.paperflow.app.domain.usecase.GetDocumentsByTypeUseCase;
import com.paperflow.app.domain.usecase.GetFavoriteDocumentsUseCase;
import com.paperflow.app.domain.usecase.GetFolderContentsUseCase;
import com.paperflow.app.domain.usecase.GetFoldersUseCase;
import com.paperflow.app.domain.usecase.GetRecentDocumentsUseCase;
import com.paperflow.app.domain.usecase.GetStorageInfoUseCase;
import com.paperflow.app.domain.usecase.ImportDocumentUseCase;
import com.paperflow.app.domain.usecase.SaveNoteUseCase;
import com.paperflow.app.domain.usecase.SaveReadingPositionUseCase;
import com.paperflow.app.domain.usecase.SaveScanAsDocumentUseCase;
import com.paperflow.app.domain.usecase.SearchDocumentsUseCase;
import com.paperflow.app.domain.usecase.ToggleFavoriteUseCase;
import com.paperflow.app.presentation.aichat.AIChatViewModel;
import com.paperflow.app.presentation.aichat.AIChatViewModel_HiltModules;
import com.paperflow.app.presentation.annotations.AnnotationViewModel;
import com.paperflow.app.presentation.annotations.AnnotationViewModel_HiltModules;
import com.paperflow.app.presentation.convert.ConvertViewModel;
import com.paperflow.app.presentation.convert.ConvertViewModel_HiltModules;
import com.paperflow.app.presentation.home.HomeViewModel;
import com.paperflow.app.presentation.home.HomeViewModel_HiltModules;
import com.paperflow.app.presentation.notes.NoteEditorViewModel;
import com.paperflow.app.presentation.notes.NoteEditorViewModel_HiltModules;
import com.paperflow.app.presentation.notes.NotesViewModel;
import com.paperflow.app.presentation.notes.NotesViewModel_HiltModules;
import com.paperflow.app.presentation.onboarding.OnboardingViewModel;
import com.paperflow.app.presentation.onboarding.OnboardingViewModel_HiltModules;
import com.paperflow.app.presentation.pdfviewer.PDFViewerViewModel;
import com.paperflow.app.presentation.pdfviewer.PDFViewerViewModel_HiltModules;
import com.paperflow.app.presentation.scanner.EditScanViewModel;
import com.paperflow.app.presentation.scanner.EditScanViewModel_HiltModules;
import com.paperflow.app.presentation.scanner.ScannerViewModel;
import com.paperflow.app.presentation.scanner.ScannerViewModel_HiltModules;
import com.paperflow.app.presentation.search.SearchViewModel;
import com.paperflow.app.presentation.search.SearchViewModel_HiltModules;
import com.paperflow.app.presentation.settings.SettingsViewModel;
import com.paperflow.app.presentation.settings.SettingsViewModel_HiltModules;
import com.paperflow.app.presentation.share.SharePrintViewModel;
import com.paperflow.app.presentation.share.SharePrintViewModel_HiltModules;
import com.paperflow.app.presentation.upload.UploadViewModel;
import com.paperflow.app.presentation.upload.UploadViewModel_HiltModules;
import com.paperflow.app.presentation.vault.VaultViewModel;
import com.paperflow.app.presentation.vault.VaultViewModel_HiltModules;
import com.paperflow.app.presentation.workspace.DocumentDetailsViewModel;
import com.paperflow.app.presentation.workspace.DocumentDetailsViewModel_HiltModules;
import com.paperflow.app.presentation.workspace.WorkspaceViewModel;
import com.paperflow.app.presentation.workspace.WorkspaceViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerPaperFlowApp_HiltComponents_SingletonC {
  private DaggerPaperFlowApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PaperFlowApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PaperFlowApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PaperFlowApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PaperFlowApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PaperFlowApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PaperFlowApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PaperFlowApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PaperFlowApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PaperFlowApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PaperFlowApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PaperFlowApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PaperFlowApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PaperFlowApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(17).put(LazyClassKeyProvider.com_paperflow_app_presentation_aichat_AIChatViewModel, AIChatViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_annotations_AnnotationViewModel, AnnotationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_convert_ConvertViewModel, ConvertViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_workspace_DocumentDetailsViewModel, DocumentDetailsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_scanner_EditScanViewModel, EditScanViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_notes_NoteEditorViewModel, NoteEditorViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_notes_NotesViewModel, NotesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_pdfviewer_PDFViewerViewModel, PDFViewerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_scanner_ScannerViewModel, ScannerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_search_SearchViewModel, SearchViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_share_SharePrintViewModel, SharePrintViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_upload_UploadViewModel, UploadViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_vault_VaultViewModel, VaultViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_paperflow_app_presentation_workspace_WorkspaceViewModel, WorkspaceViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPrefs(instance, singletonCImpl.preferencesDataStoreProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_paperflow_app_presentation_convert_ConvertViewModel = "com.paperflow.app.presentation.convert.ConvertViewModel";

      static String com_paperflow_app_presentation_scanner_ScannerViewModel = "com.paperflow.app.presentation.scanner.ScannerViewModel";

      static String com_paperflow_app_presentation_share_SharePrintViewModel = "com.paperflow.app.presentation.share.SharePrintViewModel";

      static String com_paperflow_app_presentation_aichat_AIChatViewModel = "com.paperflow.app.presentation.aichat.AIChatViewModel";

      static String com_paperflow_app_presentation_workspace_DocumentDetailsViewModel = "com.paperflow.app.presentation.workspace.DocumentDetailsViewModel";

      static String com_paperflow_app_presentation_notes_NoteEditorViewModel = "com.paperflow.app.presentation.notes.NoteEditorViewModel";

      static String com_paperflow_app_presentation_scanner_EditScanViewModel = "com.paperflow.app.presentation.scanner.EditScanViewModel";

      static String com_paperflow_app_presentation_onboarding_OnboardingViewModel = "com.paperflow.app.presentation.onboarding.OnboardingViewModel";

      static String com_paperflow_app_presentation_search_SearchViewModel = "com.paperflow.app.presentation.search.SearchViewModel";

      static String com_paperflow_app_presentation_annotations_AnnotationViewModel = "com.paperflow.app.presentation.annotations.AnnotationViewModel";

      static String com_paperflow_app_presentation_notes_NotesViewModel = "com.paperflow.app.presentation.notes.NotesViewModel";

      static String com_paperflow_app_presentation_workspace_WorkspaceViewModel = "com.paperflow.app.presentation.workspace.WorkspaceViewModel";

      static String com_paperflow_app_presentation_pdfviewer_PDFViewerViewModel = "com.paperflow.app.presentation.pdfviewer.PDFViewerViewModel";

      static String com_paperflow_app_presentation_vault_VaultViewModel = "com.paperflow.app.presentation.vault.VaultViewModel";

      static String com_paperflow_app_presentation_home_HomeViewModel = "com.paperflow.app.presentation.home.HomeViewModel";

      static String com_paperflow_app_presentation_settings_SettingsViewModel = "com.paperflow.app.presentation.settings.SettingsViewModel";

      static String com_paperflow_app_presentation_upload_UploadViewModel = "com.paperflow.app.presentation.upload.UploadViewModel";

      @KeepFieldType
      ConvertViewModel com_paperflow_app_presentation_convert_ConvertViewModel2;

      @KeepFieldType
      ScannerViewModel com_paperflow_app_presentation_scanner_ScannerViewModel2;

      @KeepFieldType
      SharePrintViewModel com_paperflow_app_presentation_share_SharePrintViewModel2;

      @KeepFieldType
      AIChatViewModel com_paperflow_app_presentation_aichat_AIChatViewModel2;

      @KeepFieldType
      DocumentDetailsViewModel com_paperflow_app_presentation_workspace_DocumentDetailsViewModel2;

      @KeepFieldType
      NoteEditorViewModel com_paperflow_app_presentation_notes_NoteEditorViewModel2;

      @KeepFieldType
      EditScanViewModel com_paperflow_app_presentation_scanner_EditScanViewModel2;

      @KeepFieldType
      OnboardingViewModel com_paperflow_app_presentation_onboarding_OnboardingViewModel2;

      @KeepFieldType
      SearchViewModel com_paperflow_app_presentation_search_SearchViewModel2;

      @KeepFieldType
      AnnotationViewModel com_paperflow_app_presentation_annotations_AnnotationViewModel2;

      @KeepFieldType
      NotesViewModel com_paperflow_app_presentation_notes_NotesViewModel2;

      @KeepFieldType
      WorkspaceViewModel com_paperflow_app_presentation_workspace_WorkspaceViewModel2;

      @KeepFieldType
      PDFViewerViewModel com_paperflow_app_presentation_pdfviewer_PDFViewerViewModel2;

      @KeepFieldType
      VaultViewModel com_paperflow_app_presentation_vault_VaultViewModel2;

      @KeepFieldType
      HomeViewModel com_paperflow_app_presentation_home_HomeViewModel2;

      @KeepFieldType
      SettingsViewModel com_paperflow_app_presentation_settings_SettingsViewModel2;

      @KeepFieldType
      UploadViewModel com_paperflow_app_presentation_upload_UploadViewModel2;
    }
  }

  private static final class ViewModelCImpl extends PaperFlowApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AIChatViewModel> aIChatViewModelProvider;

    private Provider<AnnotationViewModel> annotationViewModelProvider;

    private Provider<ConvertViewModel> convertViewModelProvider;

    private Provider<DocumentDetailsViewModel> documentDetailsViewModelProvider;

    private Provider<EditScanViewModel> editScanViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<NoteEditorViewModel> noteEditorViewModelProvider;

    private Provider<NotesViewModel> notesViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PDFViewerViewModel> pDFViewerViewModelProvider;

    private Provider<ScannerViewModel> scannerViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SharePrintViewModel> sharePrintViewModelProvider;

    private Provider<UploadViewModel> uploadViewModelProvider;

    private Provider<VaultViewModel> vaultViewModelProvider;

    private Provider<WorkspaceViewModel> workspaceViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetDocumentByIdUseCase getDocumentByIdUseCase() {
      return new GetDocumentByIdUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    private ConvertScanToNoteUseCase convertScanToNoteUseCase() {
      return new ConvertScanToNoteUseCase(singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.pageRepositoryImplProvider.get(), singletonCImpl.noteRepositoryImplProvider.get());
    }

    private ToggleFavoriteUseCase toggleFavoriteUseCase() {
      return new ToggleFavoriteUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    private DeleteDocumentUseCase deleteDocumentUseCase() {
      return new DeleteDocumentUseCase(singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.pageRepositoryImplProvider.get(), singletonCImpl.oCRRepositoryImplProvider.get(), singletonCImpl.provideFileStorageProvider.get());
    }

    private SaveScanAsDocumentUseCase saveScanAsDocumentUseCase() {
      return new SaveScanAsDocumentUseCase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.pageRepositoryImplProvider.get(), singletonCImpl.provideFileStorageProvider.get(), singletonCImpl.providePDFEngineProvider.get());
    }

    private GetRecentDocumentsUseCase getRecentDocumentsUseCase() {
      return new GetRecentDocumentsUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    private GetFoldersUseCase getFoldersUseCase() {
      return new GetFoldersUseCase(singletonCImpl.folderRepositoryImplProvider.get());
    }

    private GetStorageInfoUseCase getStorageInfoUseCase() {
      return new GetStorageInfoUseCase(singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.provideFileStorageProvider.get());
    }

    private GetAllNotesUseCase getAllNotesUseCase() {
      return new GetAllNotesUseCase(singletonCImpl.noteRepositoryImplProvider.get());
    }

    private CreateNoteUseCase createNoteUseCase() {
      return new CreateNoteUseCase(singletonCImpl.noteRepositoryImplProvider.get());
    }

    private SaveNoteUseCase saveNoteUseCase() {
      return new SaveNoteUseCase(singletonCImpl.noteRepositoryImplProvider.get());
    }

    private AutoSaveNoteUseCase autoSaveNoteUseCase() {
      return new AutoSaveNoteUseCase(singletonCImpl.noteRepositoryImplProvider.get());
    }

    private DeleteNoteUseCase deleteNoteUseCase() {
      return new DeleteNoteUseCase(singletonCImpl.noteRepositoryImplProvider.get());
    }

    private SaveReadingPositionUseCase saveReadingPositionUseCase() {
      return new SaveReadingPositionUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    private GetAnnotationsUseCase getAnnotationsUseCase() {
      return new GetAnnotationsUseCase(singletonCImpl.annotationRepositoryImplProvider.get());
    }

    private SearchDocumentsUseCase searchDocumentsUseCase() {
      return new SearchDocumentsUseCase(singletonCImpl.searchRepositoryImplProvider.get());
    }

    private ImportDocumentUseCase importDocumentUseCase() {
      return new ImportDocumentUseCase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.pageRepositoryImplProvider.get(), singletonCImpl.provideFileStorageProvider.get(), singletonCImpl.providePDFEngineProvider.get());
    }

    private GetAllDocumentsUseCase getAllDocumentsUseCase() {
      return new GetAllDocumentsUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    private GetFolderContentsUseCase getFolderContentsUseCase() {
      return new GetFolderContentsUseCase(singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.folderRepositoryImplProvider.get());
    }

    private CreateFolderUseCase createFolderUseCase() {
      return new CreateFolderUseCase(singletonCImpl.folderRepositoryImplProvider.get());
    }

    private GetDocumentsByTypeUseCase getDocumentsByTypeUseCase() {
      return new GetDocumentsByTypeUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    private GetFavoriteDocumentsUseCase getFavoriteDocumentsUseCase() {
      return new GetFavoriteDocumentsUseCase(singletonCImpl.documentRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.aIChatViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.annotationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.convertViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.documentDetailsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.editScanViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.noteEditorViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.notesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.pDFViewerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.scannerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.sharePrintViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.uploadViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
      this.vaultViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 15);
      this.workspaceViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 16);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(17).put(LazyClassKeyProvider.com_paperflow_app_presentation_aichat_AIChatViewModel, ((Provider) aIChatViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_annotations_AnnotationViewModel, ((Provider) annotationViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_convert_ConvertViewModel, ((Provider) convertViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_workspace_DocumentDetailsViewModel, ((Provider) documentDetailsViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_scanner_EditScanViewModel, ((Provider) editScanViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_notes_NoteEditorViewModel, ((Provider) noteEditorViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_notes_NotesViewModel, ((Provider) notesViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_pdfviewer_PDFViewerViewModel, ((Provider) pDFViewerViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_scanner_ScannerViewModel, ((Provider) scannerViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_search_SearchViewModel, ((Provider) searchViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_share_SharePrintViewModel, ((Provider) sharePrintViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_upload_UploadViewModel, ((Provider) uploadViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_vault_VaultViewModel, ((Provider) vaultViewModelProvider)).put(LazyClassKeyProvider.com_paperflow_app_presentation_workspace_WorkspaceViewModel, ((Provider) workspaceViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_paperflow_app_presentation_workspace_WorkspaceViewModel = "com.paperflow.app.presentation.workspace.WorkspaceViewModel";

      static String com_paperflow_app_presentation_upload_UploadViewModel = "com.paperflow.app.presentation.upload.UploadViewModel";

      static String com_paperflow_app_presentation_onboarding_OnboardingViewModel = "com.paperflow.app.presentation.onboarding.OnboardingViewModel";

      static String com_paperflow_app_presentation_notes_NotesViewModel = "com.paperflow.app.presentation.notes.NotesViewModel";

      static String com_paperflow_app_presentation_pdfviewer_PDFViewerViewModel = "com.paperflow.app.presentation.pdfviewer.PDFViewerViewModel";

      static String com_paperflow_app_presentation_notes_NoteEditorViewModel = "com.paperflow.app.presentation.notes.NoteEditorViewModel";

      static String com_paperflow_app_presentation_aichat_AIChatViewModel = "com.paperflow.app.presentation.aichat.AIChatViewModel";

      static String com_paperflow_app_presentation_home_HomeViewModel = "com.paperflow.app.presentation.home.HomeViewModel";

      static String com_paperflow_app_presentation_annotations_AnnotationViewModel = "com.paperflow.app.presentation.annotations.AnnotationViewModel";

      static String com_paperflow_app_presentation_convert_ConvertViewModel = "com.paperflow.app.presentation.convert.ConvertViewModel";

      static String com_paperflow_app_presentation_scanner_ScannerViewModel = "com.paperflow.app.presentation.scanner.ScannerViewModel";

      static String com_paperflow_app_presentation_settings_SettingsViewModel = "com.paperflow.app.presentation.settings.SettingsViewModel";

      static String com_paperflow_app_presentation_scanner_EditScanViewModel = "com.paperflow.app.presentation.scanner.EditScanViewModel";

      static String com_paperflow_app_presentation_search_SearchViewModel = "com.paperflow.app.presentation.search.SearchViewModel";

      static String com_paperflow_app_presentation_share_SharePrintViewModel = "com.paperflow.app.presentation.share.SharePrintViewModel";

      static String com_paperflow_app_presentation_workspace_DocumentDetailsViewModel = "com.paperflow.app.presentation.workspace.DocumentDetailsViewModel";

      static String com_paperflow_app_presentation_vault_VaultViewModel = "com.paperflow.app.presentation.vault.VaultViewModel";

      @KeepFieldType
      WorkspaceViewModel com_paperflow_app_presentation_workspace_WorkspaceViewModel2;

      @KeepFieldType
      UploadViewModel com_paperflow_app_presentation_upload_UploadViewModel2;

      @KeepFieldType
      OnboardingViewModel com_paperflow_app_presentation_onboarding_OnboardingViewModel2;

      @KeepFieldType
      NotesViewModel com_paperflow_app_presentation_notes_NotesViewModel2;

      @KeepFieldType
      PDFViewerViewModel com_paperflow_app_presentation_pdfviewer_PDFViewerViewModel2;

      @KeepFieldType
      NoteEditorViewModel com_paperflow_app_presentation_notes_NoteEditorViewModel2;

      @KeepFieldType
      AIChatViewModel com_paperflow_app_presentation_aichat_AIChatViewModel2;

      @KeepFieldType
      HomeViewModel com_paperflow_app_presentation_home_HomeViewModel2;

      @KeepFieldType
      AnnotationViewModel com_paperflow_app_presentation_annotations_AnnotationViewModel2;

      @KeepFieldType
      ConvertViewModel com_paperflow_app_presentation_convert_ConvertViewModel2;

      @KeepFieldType
      ScannerViewModel com_paperflow_app_presentation_scanner_ScannerViewModel2;

      @KeepFieldType
      SettingsViewModel com_paperflow_app_presentation_settings_SettingsViewModel2;

      @KeepFieldType
      EditScanViewModel com_paperflow_app_presentation_scanner_EditScanViewModel2;

      @KeepFieldType
      SearchViewModel com_paperflow_app_presentation_search_SearchViewModel2;

      @KeepFieldType
      SharePrintViewModel com_paperflow_app_presentation_share_SharePrintViewModel2;

      @KeepFieldType
      DocumentDetailsViewModel com_paperflow_app_presentation_workspace_DocumentDetailsViewModel2;

      @KeepFieldType
      VaultViewModel com_paperflow_app_presentation_vault_VaultViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.paperflow.app.presentation.aichat.AIChatViewModel 
          return (T) new AIChatViewModel(singletonCImpl.searchRepositoryImplProvider.get(), singletonCImpl.pageRepositoryImplProvider.get(), viewModelCImpl.getDocumentByIdUseCase(), singletonCImpl.preferencesDataStoreProvider.get());

          case 1: // com.paperflow.app.presentation.annotations.AnnotationViewModel 
          return (T) new AnnotationViewModel(viewModelCImpl.getDocumentByIdUseCase(), singletonCImpl.annotationRepositoryImplProvider.get(), singletonCImpl.providePDFEngineProvider.get());

          case 2: // com.paperflow.app.presentation.convert.ConvertViewModel 
          return (T) new ConvertViewModel(viewModelCImpl.getDocumentByIdUseCase(), viewModelCImpl.convertScanToNoteUseCase());

          case 3: // com.paperflow.app.presentation.workspace.DocumentDetailsViewModel 
          return (T) new DocumentDetailsViewModel(viewModelCImpl.getDocumentByIdUseCase(), viewModelCImpl.toggleFavoriteUseCase(), viewModelCImpl.deleteDocumentUseCase(), viewModelCImpl.convertScanToNoteUseCase());

          case 4: // com.paperflow.app.presentation.scanner.EditScanViewModel 
          return (T) new EditScanViewModel(singletonCImpl.provideFileStorageProvider.get(), singletonCImpl.provideScanProcessorProvider.get(), viewModelCImpl.saveScanAsDocumentUseCase());

          case 5: // com.paperflow.app.presentation.home.HomeViewModel 
          return (T) new HomeViewModel(viewModelCImpl.getRecentDocumentsUseCase(), viewModelCImpl.getFoldersUseCase(), viewModelCImpl.getStorageInfoUseCase(), viewModelCImpl.toggleFavoriteUseCase(), viewModelCImpl.deleteDocumentUseCase(), singletonCImpl.preferencesDataStoreProvider.get());

          case 6: // com.paperflow.app.presentation.notes.NoteEditorViewModel 
          return (T) new NoteEditorViewModel(viewModelCImpl.getAllNotesUseCase(), viewModelCImpl.createNoteUseCase(), viewModelCImpl.saveNoteUseCase(), viewModelCImpl.autoSaveNoteUseCase(), viewModelCImpl.deleteNoteUseCase());

          case 7: // com.paperflow.app.presentation.notes.NotesViewModel 
          return (T) new NotesViewModel(viewModelCImpl.getAllNotesUseCase(), viewModelCImpl.createNoteUseCase(), viewModelCImpl.deleteNoteUseCase(), viewModelCImpl.toggleFavoriteUseCase(), viewModelCImpl.getAllNotesUseCase());

          case 8: // com.paperflow.app.presentation.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.preferencesDataStoreProvider.get());

          case 9: // com.paperflow.app.presentation.pdfviewer.PDFViewerViewModel 
          return (T) new PDFViewerViewModel(viewModelCImpl.getDocumentByIdUseCase(), viewModelCImpl.saveReadingPositionUseCase(), viewModelCImpl.getAnnotationsUseCase(), viewModelCImpl.toggleFavoriteUseCase(), singletonCImpl.activityRepositoryImplProvider.get(), singletonCImpl.providePDFEngineProvider.get(), singletonCImpl.provideFileStorageProvider.get());

          case 10: // com.paperflow.app.presentation.scanner.ScannerViewModel 
          return (T) new ScannerViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideFileStorageProvider.get(), singletonCImpl.provideScanProcessorProvider.get());

          case 11: // com.paperflow.app.presentation.search.SearchViewModel 
          return (T) new SearchViewModel(viewModelCImpl.searchDocumentsUseCase());

          case 12: // com.paperflow.app.presentation.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.preferencesDataStoreProvider.get(), singletonCImpl.documentRepositoryImplProvider.get(), singletonCImpl.provideFileStorageProvider.get());

          case 13: // com.paperflow.app.presentation.share.SharePrintViewModel 
          return (T) new SharePrintViewModel(viewModelCImpl.getDocumentByIdUseCase());

          case 14: // com.paperflow.app.presentation.upload.UploadViewModel 
          return (T) new UploadViewModel(viewModelCImpl.importDocumentUseCase());

          case 15: // com.paperflow.app.presentation.vault.VaultViewModel 
          return (T) new VaultViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.preferencesDataStoreProvider.get());

          case 16: // com.paperflow.app.presentation.workspace.WorkspaceViewModel 
          return (T) new WorkspaceViewModel(viewModelCImpl.getAllDocumentsUseCase(), viewModelCImpl.getFolderContentsUseCase(), viewModelCImpl.getFoldersUseCase(), viewModelCImpl.toggleFavoriteUseCase(), viewModelCImpl.deleteDocumentUseCase(), viewModelCImpl.createFolderUseCase(), viewModelCImpl.getDocumentsByTypeUseCase(), viewModelCImpl.getFavoriteDocumentsUseCase(), viewModelCImpl.getStorageInfoUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PaperFlowApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PaperFlowApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PaperFlowApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<OCRRepositoryImpl> oCRRepositoryImplProvider;

    private Provider<FileStorage> provideFileStorageProvider;

    private Provider<PDFEngine> providePDFEngineProvider;

    private Provider<OCRWorker_AssistedFactory> oCRWorker_AssistedFactoryProvider;

    private Provider<ThumbnailWorker_AssistedFactory> thumbnailWorker_AssistedFactoryProvider;

    private Provider<PreferencesDataStore> preferencesDataStoreProvider;

    private Provider<SearchRepositoryImpl> searchRepositoryImplProvider;

    private Provider<PageRepositoryImpl> pageRepositoryImplProvider;

    private Provider<DocumentRepositoryImpl> documentRepositoryImplProvider;

    private Provider<AnnotationRepositoryImpl> annotationRepositoryImplProvider;

    private Provider<NoteRepositoryImpl> noteRepositoryImplProvider;

    private Provider<ScanProcessor> provideScanProcessorProvider;

    private Provider<FolderRepositoryImpl> folderRepositoryImplProvider;

    private Provider<ActivityRepositoryImpl> activityRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private DocumentDao documentDao() {
      return DatabaseModule_ProvideDocumentDaoFactory.provideDocumentDao(provideDatabaseProvider.get());
    }

    private PageDao pageDao() {
      return DatabaseModule_ProvidePageDaoFactory.providePageDao(provideDatabaseProvider.get());
    }

    private OCRIndexDao oCRIndexDao() {
      return DatabaseModule_ProvideOCRIndexDaoFactory.provideOCRIndexDao(provideDatabaseProvider.get());
    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return ImmutableMap.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>of("com.paperflow.app.data.workers.OCRWorker", ((Provider) oCRWorker_AssistedFactoryProvider), "com.paperflow.app.data.workers.ThumbnailWorker", ((Provider) thumbnailWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private NoteDao noteDao() {
      return DatabaseModule_ProvideNoteDaoFactory.provideNoteDao(provideDatabaseProvider.get());
    }

    private FolderDao folderDao() {
      return DatabaseModule_ProvideFolderDaoFactory.provideFolderDao(provideDatabaseProvider.get());
    }

    private AnnotationDao annotationDao() {
      return DatabaseModule_ProvideAnnotationDaoFactory.provideAnnotationDao(provideDatabaseProvider.get());
    }

    private ActivityDao activityDao() {
      return DatabaseModule_ProvideActivityDaoFactory.provideActivityDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.oCRRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<OCRRepositoryImpl>(singletonCImpl, 2));
      this.provideFileStorageProvider = DoubleCheck.provider(new SwitchingProvider<FileStorage>(singletonCImpl, 3));
      this.providePDFEngineProvider = DoubleCheck.provider(new SwitchingProvider<PDFEngine>(singletonCImpl, 4));
      this.oCRWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<OCRWorker_AssistedFactory>(singletonCImpl, 0));
      this.thumbnailWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ThumbnailWorker_AssistedFactory>(singletonCImpl, 5));
      this.preferencesDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<PreferencesDataStore>(singletonCImpl, 6));
      this.searchRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SearchRepositoryImpl>(singletonCImpl, 7));
      this.pageRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<PageRepositoryImpl>(singletonCImpl, 8));
      this.documentRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<DocumentRepositoryImpl>(singletonCImpl, 9));
      this.annotationRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AnnotationRepositoryImpl>(singletonCImpl, 10));
      this.noteRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<NoteRepositoryImpl>(singletonCImpl, 11));
      this.provideScanProcessorProvider = DoubleCheck.provider(new SwitchingProvider<ScanProcessor>(singletonCImpl, 12));
      this.folderRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<FolderRepositoryImpl>(singletonCImpl, 13));
      this.activityRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRepositoryImpl>(singletonCImpl, 14));
    }

    @Override
    public void injectPaperFlowApp(PaperFlowApp paperFlowApp) {
      injectPaperFlowApp2(paperFlowApp);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private PaperFlowApp injectPaperFlowApp2(PaperFlowApp instance) {
      PaperFlowApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.paperflow.app.data.workers.OCRWorker_AssistedFactory 
          return (T) new OCRWorker_AssistedFactory() {
            @Override
            public OCRWorker create(Context context, WorkerParameters workerParams) {
              return new OCRWorker(context, workerParams, singletonCImpl.documentDao(), singletonCImpl.pageDao(), singletonCImpl.oCRRepositoryImplProvider.get(), singletonCImpl.provideFileStorageProvider.get(), singletonCImpl.providePDFEngineProvider.get());
            }
          };

          case 1: // com.paperflow.app.data.local.database.AppDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.paperflow.app.data.repository.OCRRepositoryImpl 
          return (T) new OCRRepositoryImpl(singletonCImpl.oCRIndexDao());

          case 3: // com.paperflow.app.data.local.file.FileStorage 
          return (T) AppModule_ProvideFileStorageFactory.provideFileStorage(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.paperflow.app.data.local.file.PDFEngine 
          return (T) AppModule_ProvidePDFEngineFactory.providePDFEngine(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideFileStorageProvider.get());

          case 5: // com.paperflow.app.data.workers.ThumbnailWorker_AssistedFactory 
          return (T) new ThumbnailWorker_AssistedFactory() {
            @Override
            public ThumbnailWorker create(Context context2, WorkerParameters workerParams2) {
              return new ThumbnailWorker(context2, workerParams2, singletonCImpl.documentDao(), singletonCImpl.pageDao(), singletonCImpl.providePDFEngineProvider.get(), singletonCImpl.provideFileStorageProvider.get());
            }
          };

          case 6: // com.paperflow.app.data.local.datastore.PreferencesDataStore 
          return (T) new PreferencesDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.paperflow.app.data.repository.SearchRepositoryImpl 
          return (T) new SearchRepositoryImpl(singletonCImpl.oCRIndexDao(), singletonCImpl.documentDao(), singletonCImpl.noteDao(), singletonCImpl.folderDao());

          case 8: // com.paperflow.app.data.repository.PageRepositoryImpl 
          return (T) new PageRepositoryImpl(singletonCImpl.pageDao());

          case 9: // com.paperflow.app.data.repository.DocumentRepositoryImpl 
          return (T) new DocumentRepositoryImpl(singletonCImpl.documentDao());

          case 10: // com.paperflow.app.data.repository.AnnotationRepositoryImpl 
          return (T) new AnnotationRepositoryImpl(singletonCImpl.annotationDao());

          case 11: // com.paperflow.app.data.repository.NoteRepositoryImpl 
          return (T) new NoteRepositoryImpl(singletonCImpl.noteDao());

          case 12: // com.paperflow.app.data.local.file.ScanProcessor 
          return (T) AppModule_ProvideScanProcessorFactory.provideScanProcessor(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideFileStorageProvider.get());

          case 13: // com.paperflow.app.data.repository.FolderRepositoryImpl 
          return (T) new FolderRepositoryImpl(singletonCImpl.folderDao(), singletonCImpl.documentDao());

          case 14: // com.paperflow.app.data.repository.ActivityRepositoryImpl 
          return (T) new ActivityRepositoryImpl(singletonCImpl.activityDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
