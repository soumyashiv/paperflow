package com.paperflow.app.domain.usecase;

import com.paperflow.app.domain.repository.NoteRepository;
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
public final class DeleteNoteUseCase_Factory implements Factory<DeleteNoteUseCase> {
  private final Provider<NoteRepository> repoProvider;

  public DeleteNoteUseCase_Factory(Provider<NoteRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public DeleteNoteUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static DeleteNoteUseCase_Factory create(Provider<NoteRepository> repoProvider) {
    return new DeleteNoteUseCase_Factory(repoProvider);
  }

  public static DeleteNoteUseCase newInstance(NoteRepository repo) {
    return new DeleteNoteUseCase(repo);
  }
}
