package com.paperflow.app.presentation.annotations;

import com.paperflow.app.data.local.file.PDFEngine;
import com.paperflow.app.domain.repository.AnnotationRepository;
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
public final class AnnotationViewModel_Factory implements Factory<AnnotationViewModel> {
  private final Provider<GetDocumentByIdUseCase> getDocumentProvider;

  private final Provider<AnnotationRepository> annotationRepoProvider;

  private final Provider<PDFEngine> pdfEngineProvider;

  public AnnotationViewModel_Factory(Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<AnnotationRepository> annotationRepoProvider,
      Provider<PDFEngine> pdfEngineProvider) {
    this.getDocumentProvider = getDocumentProvider;
    this.annotationRepoProvider = annotationRepoProvider;
    this.pdfEngineProvider = pdfEngineProvider;
  }

  @Override
  public AnnotationViewModel get() {
    return newInstance(getDocumentProvider.get(), annotationRepoProvider.get(), pdfEngineProvider.get());
  }

  public static AnnotationViewModel_Factory create(
      Provider<GetDocumentByIdUseCase> getDocumentProvider,
      Provider<AnnotationRepository> annotationRepoProvider,
      Provider<PDFEngine> pdfEngineProvider) {
    return new AnnotationViewModel_Factory(getDocumentProvider, annotationRepoProvider, pdfEngineProvider);
  }

  public static AnnotationViewModel newInstance(GetDocumentByIdUseCase getDocument,
      AnnotationRepository annotationRepo, PDFEngine pdfEngine) {
    return new AnnotationViewModel(getDocument, annotationRepo, pdfEngine);
  }
}
