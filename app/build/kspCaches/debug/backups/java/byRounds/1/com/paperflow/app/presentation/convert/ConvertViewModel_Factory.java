package com.paperflow.app.presentation.convert;

import com.paperflow.app.domain.usecase.ConvertScanToNoteUseCase;
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase;
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
public final class ConvertViewModel_Factory implements Factory<ConvertViewModel> {
  private final Provider<GetDocumentByIdUseCase> getDocumentProvider;

  private final Provider<ConvertScanToNoteUseCase> convertToNoteProvider;

  public ConvertViewModel_Factory(Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<ConvertScanToNoteUseCase> convertToNoteProvider) {
    this.getDocumentProvider = getDocumentProvider;
    this.convertToNoteProvider = convertToNoteProvider;
  }

  @Override
  public ConvertViewModel get() {
    return newInstance(getDocumentProvider.get(), convertToNoteProvider.get());
  }

  public static ConvertViewModel_Factory create(
      Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<ConvertScanToNoteUseCase> convertToNoteProvider) {
    return new ConvertViewModel_Factory(getDocumentProvider, convertToNoteProvider);
  }

  public static ConvertViewModel newInstance(GetDocumentByIdUseCase getDocument,
      ConvertScanToNoteUseCase convertToNote) {
    return new ConvertViewModel(getDocument, convertToNote);
  }
}
