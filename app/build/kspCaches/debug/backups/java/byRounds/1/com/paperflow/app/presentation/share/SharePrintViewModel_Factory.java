package com.paperflow.app.presentation.share;

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
public final class SharePrintViewModel_Factory implements Factory<SharePrintViewModel> {
  private final Provider<GetDocumentByIdUseCase> getDocumentProvider;

  public SharePrintViewModel_Factory(Provider<GetDocumentByIdUseCase> getDocumentProvider) {
    this.getDocumentProvider = getDocumentProvider;
  }

  @Override
  public SharePrintViewModel get() {
    return newInstance(getDocumentProvider.get());
  }

  public static SharePrintViewModel_Factory create(
      Provider<GetDocumentByIdUseCase> getDocumentProvider) {
    return new SharePrintViewModel_Factory(getDocumentProvider);
  }

  public static SharePrintViewModel newInstance(GetDocumentByIdUseCase getDocument) {
    return new SharePrintViewModel(getDocument);
  }
}
