package com.paperflow.app.presentation.pdfviewer;

import com.paperflow.app.data.local.file.FileStorage;
import com.paperflow.app.data.local.file.PDFEngine;
import com.paperflow.app.domain.repository.ActivityRepository;
import com.paperflow.app.domain.usecase.GetAnnotationsUseCase;
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase;
import com.paperflow.app.domain.usecase.SaveReadingPositionUseCase;
import com.paperflow.app.domain.usecase.ToggleFavoriteUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PDFViewerViewModel_Factory implements Factory<PDFViewerViewModel> {
  private final Provider<GetDocumentByIdUseCase> getDocumentByIdProvider;

  private final Provider<SaveReadingPositionUseCase> saveReadingPositionProvider;

  private final Provider<GetAnnotationsUseCase> getAnnotationsProvider;

  private final Provider<ToggleFavoriteUseCase> toggleFavoriteProvider;

  private final Provider<ActivityRepository> activityRepoProvider;

  private final Provider<PDFEngine> pdfEngineProvider;

  private final Provider<FileStorage> storageProvider;

  public PDFViewerViewModel_Factory(Provider<GetDocumentByIdUseCase> getDocumentByIdProvider,
      Provider<SaveReadingPositionUseCase> saveReadingPositionProvider,
      Provider<GetAnnotationsUseCase> getAnnotationsProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<ActivityRepository> activityRepoProvider, Provider<PDFEngine> pdfEngineProvider,
      Provider<FileStorage> storageProvider) {
    this.getDocumentByIdProvider = getDocumentByIdProvider;
    this.saveReadingPositionProvider = saveReadingPositionProvider;
    this.getAnnotationsProvider = getAnnotationsProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
    this.activityRepoProvider = activityRepoProvider;
    this.pdfEngineProvider = pdfEngineProvider;
    this.storageProvider = storageProvider;
  }

  @Override
  public PDFViewerViewModel get() {
    return newInstance(getDocumentByIdProvider.get(), saveReadingPositionProvider.get(), getAnnotationsProvider.get(), toggleFavoriteProvider.get(), activityRepoProvider.get(), pdfEngineProvider.get(), storageProvider.get());
  }

  public static PDFViewerViewModel_Factory create(
      Provider<GetDocumentByIdUseCase> getDocumentByIdProvider,
      Provider<SaveReadingPositionUseCase> saveReadingPositionProvider,
      Provider<GetAnnotationsUseCase> getAnnotationsProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<ActivityRepository> activityRepoProvider, Provider<PDFEngine> pdfEngineProvider,
      Provider<FileStorage> storageProvider) {
    return new PDFViewerViewModel_Factory(getDocumentByIdProvider, saveReadingPositionProvider, getAnnotationsProvider, toggleFavoriteProvider, activityRepoProvider, pdfEngineProvider, storageProvider);
  }

  public static PDFViewerViewModel newInstance(GetDocumentByIdUseCase getDocumentById,
      SaveReadingPositionUseCase saveReadingPosition, GetAnnotationsUseCase getAnnotations,
      ToggleFavoriteUseCase toggleFavorite, ActivityRepository activityRepo, PDFEngine pdfEngine,
      FileStorage storage) {
    return new PDFViewerViewModel(getDocumentById, saveReadingPosition, getAnnotations, toggleFavorite, activityRepo, pdfEngine, storage);
  }
}
