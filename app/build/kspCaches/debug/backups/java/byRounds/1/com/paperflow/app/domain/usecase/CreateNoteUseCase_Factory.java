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
public final class CreateNoteUseCase_Factory implements Factory<CreateNoteUseCase> {
  private final Provider<NoteRepository> repoProvider;

  public CreateNoteUseCase_Factory(Provider<NoteRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public CreateNoteUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static CreateNoteUseCase_Factory create(Provider<NoteRepository> repoProvider) {
    return new CreateNoteUseCase_Factory(repoProvider);
  }

  public static CreateNoteUseCase newInstance(NoteRepository repo) {
    return new CreateNoteUseCase(repo);
  }
}
