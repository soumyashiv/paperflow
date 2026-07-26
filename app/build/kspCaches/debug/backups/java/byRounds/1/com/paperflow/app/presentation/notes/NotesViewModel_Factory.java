package com.paperflow.app.presentation.notes;

import com.paperflow.app.domain.usecase.CreateNoteUseCase;
import com.paperflow.app.domain.usecase.DeleteNoteUseCase;
import com.paperflow.app.domain.usecase.GetAllNotesUseCase;
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
public final class NotesViewModel_Factory implements Factory<NotesViewModel> {
  private final Provider<GetAllNotesUseCase> getAllNotesProvider;

  private final Provider<CreateNoteUseCase> createNoteProvider;

  private final Provider<DeleteNoteUseCase> deleteNoteProvider;

  private final Provider<ToggleFavoriteUseCase> toggleFavoriteProvider;

  private final Provider<GetAllNotesUseCase> getNotesByTypeProvider;

  public NotesViewModel_Factory(Provider<GetAllNotesUseCase> getAllNotesProvider,
      Provider<CreateNoteUseCase> createNoteProvider,
      Provider<DeleteNoteUseCase> deleteNoteProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<GetAllNotesUseCase> getNotesByTypeProvider) {
    this.getAllNotesProvider = getAllNotesProvider;
    this.createNoteProvider = createNoteProvider;
    this.deleteNoteProvider = deleteNoteProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
    this.getNotesByTypeProvider = getNotesByTypeProvider;
  }

  @Override
  public NotesViewModel get() {
    return newInstance(getAllNotesProvider.get(), createNoteProvider.get(), deleteNoteProvider.get(), toggleFavoriteProvider.get(), getNotesByTypeProvider.get());
  }

  public static NotesViewModel_Factory create(Provider<GetAllNotesUseCase> getAllNotesProvider,
      Provider<CreateNoteUseCase> createNoteProvider,
      Provider<DeleteNoteUseCase> deleteNoteProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider,
      Provider<GetAllNotesUseCase> getNotesByTypeProvider) {
    return new NotesViewModel_Factory(getAllNotesProvider, createNoteProvider, deleteNoteProvider, toggleFavoriteProvider, getNotesByTypeProvider);
  }

  public static NotesViewModel newInstance(GetAllNotesUseCase getAllNotes,
      CreateNoteUseCase createNote, DeleteNoteUseCase deleteNote,
      ToggleFavoriteUseCase toggleFavorite, GetAllNotesUseCase getNotesByType) {
    return new NotesViewModel(getAllNotes, createNote, deleteNote, toggleFavorite, getNotesByType);
  }
}
