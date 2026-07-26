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
public final class SaveNoteUseCase_Factory implements Factory<SaveNoteUseCase> {
  private final Provider<NoteRepository> repoProvider;

  public SaveNoteUseCase_Factory(Provider<NoteRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public SaveNoteUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static SaveNoteUseCase_Factory create(Provider<NoteRepository> repoProvider) {
    return new SaveNoteUseCase_Factory(repoProvider);
  }

  public static SaveNoteUseCase newInstance(NoteRepository repo) {
    return new SaveNoteUseCase(repo);
  }
}
