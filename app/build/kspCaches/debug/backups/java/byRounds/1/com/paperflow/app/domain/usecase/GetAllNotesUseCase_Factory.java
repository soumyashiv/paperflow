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
public final class GetAllNotesUseCase_Factory implements Factory<GetAllNotesUseCase> {
  private final Provider<NoteRepository> repoProvider;

  public GetAllNotesUseCase_Factory(Provider<NoteRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public GetAllNotesUseCase get() {
    return newInstance(repoProvider.get());
  }

  public static GetAllNotesUseCase_Factory create(Provider<NoteRepository> repoProvider) {
    return new GetAllNotesUseCase_Factory(repoProvider);
  }

  public static GetAllNotesUseCase newInstance(NoteRepository repo) {
    return new GetAllNotesUseCase(repo);
  }
}
