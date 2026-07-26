package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.DocumentRepository;
import com.paperflow.app.domain.repository.NoteRepository;
import com.paperflow.app.domain.repository.PageRepository;
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
public final class ConvertScanToNoteUseCase_Factory implements Factory<ConvertScanToNoteUseCase> {
  private final Provider<DocumentRepository> documentRepoProvider;

  private final Provider<PageRepository> pageRepoProvider;

  private final Provider<NoteRepository> noteRepoProvider;

  public ConvertScanToNoteUseCase_Factory(Provider<DocumentRepository> documentRepoProvider,
      Provider<PageRepository> pageRepoProvider, Provider<NoteRepository> noteRepoProvider) {
    this.documentRepoProvider = documentRepoProvider;
    this.pageRepoProvider = pageRepoProvider;
    this.noteRepoProvider = noteRepoProvider;
  }

  @Override
  public ConvertScanToNoteUseCase get() {
    return newInstance(documentRepoProvider.get(), pageRepoProvider.get(), noteRepoProvider.get());
  }

  public static ConvertScanToNoteUseCase_Factory create(
      Provider<DocumentRepository> documentRepoProvider, Provider<PageRepository> pageRepoProvider,
      Provider<NoteRepository> noteRepoProvider) {
    return new ConvertScanToNoteUseCase_Factory(documentRepoProvider, pageRepoProvider, noteRepoProvider);
  }

  public static ConvertScanToNoteUseCase newInstance(DocumentRepository documentRepo,
      PageRepository pageRepo, NoteRepository noteRepo) {
    return new ConvertScanToNoteUseCase(documentRepo, pageRepo, noteRepo);
  }
}
