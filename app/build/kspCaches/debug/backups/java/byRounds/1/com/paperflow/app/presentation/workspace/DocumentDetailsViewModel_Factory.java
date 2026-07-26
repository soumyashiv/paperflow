package com.paperflow.app.presentation.workspace;

import com.paperflow.app.domain.usecase.ConvertScanToNoteUseCase;
import com.paperflow.app.domain.usecase.DeleteDocumentUseCase;
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase;
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
public final class DocumentDetailsViewModel_Factory implements Factory<DocumentDetailsViewModel> {
  private final Provider<GetDocumentByIdUseCase> getDocumentProvider;

  private final Provider<ToggleFavoriteUseCase> toggleFavoriteProvider;

  private final Provider<DeleteDocumentUseCase> deleteDocumentProvider;

  private final Provider<ConvertScanToNoteUseCase> convertToNoteProvider;

  public DocumentDetailsViewModel_Factory(Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<DeleteDocumentUseCase> deleteDocumentProvider,
      Provider<ConvertScanToNoteUseCase> convertToNoteProvider) {
    this.getDocumentProvider = getDocumentProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
    this.deleteDocumentProvider = deleteDocumentProvider;
    this.convertToNoteProvider = convertToNoteProvider;
  }

  @Override
  public DocumentDetailsViewModel get() {
    return newInstance(getDocumentProvider.get(), toggleFavoriteProvider.get(), deleteDocumentProvider.get(), convertToNoteProvider.get());
  }

  public static DocumentDetailsViewModel_Factory create(
      Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<DeleteDocumentUseCase> deleteDocumentProvider,
      Provider<ConvertScanToNoteUseCase> convertToNoteProvider) {
    return new DocumentDetailsViewModel_Factory(getDocumentProvider, toggleFavoriteProvider, deleteDocumentProvider, convertToNoteProvider);
  }

  public static DocumentDetailsViewModel newInstance(GetDocumentByIdUseCase getDocument,
      ToggleFavoriteUseCase toggleFavorite, DeleteDocumentUseCase deleteDocument,
      ConvertScanToNoteUseCase convertToNote) {
    return new DocumentDetailsViewModel(getDocument, toggleFavorite, deleteDocument, convertToNote);
  }
}
