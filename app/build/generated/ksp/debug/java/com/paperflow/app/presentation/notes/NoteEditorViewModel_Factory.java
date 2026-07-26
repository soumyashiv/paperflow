package com.paperflow.app.presentation.notes;

import com.paperflow.app.domain.usecase.AutoSaveNoteUseCase;
import com.paperflow.app.domain.usecase.CreateNoteUseCase;
import com.paperflow.app.domain.usecase.DeleteNoteUseCase;
import com.paperflow.app.domain.usecase.GetAllNotesUseCase;
import com.paperflow.app.domain.usecase.SaveNoteUseCase;
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
public final class NoteEditorViewModel_Factory implements Factory<NoteEditorViewModel> {
  private final Provider<GetAllNotesUseCase> getAllNotesProvider;

  private final Provider<CreateNoteUseCase> createNoteProvider;

  private final Provider<SaveNoteUseCase> saveNoteProvider;

  private final Provider<AutoSaveNoteUseCase> autoSaveProvider;

  private final Provider<DeleteNoteUseCase> deleteNoteProvider;

  public NoteEditorViewModel_Factory(Provider<GetAllNotesUseCase> getAllNotesProvider,
      Provider<CreateNoteUseCase> createNoteProvider, Provider<SaveNoteUseCase> saveNoteProvider,
      Provider<AutoSaveNoteUseCase> autoSaveProvider,
      Provider<DeleteNoteUseCase> deleteNoteProvider) {
    this.getAllNotesProvider = getAllNotesProvider;
    this.createNoteProvider = createNoteProvider;
    this.saveNoteProvider = saveNoteProvider;
    this.autoSaveProvider = autoSaveProvider;
    this.deleteNoteProvider = deleteNoteProvider;
  }

  @Override
  public NoteEditorViewModel get() {
    return newInstance(getAllNotesProvider.get(), createNoteProvider.get(), saveNoteProvider.get(), autoSaveProvider.get(), deleteNoteProvider.get());
  }

  public static NoteEditorViewModel_Factory create(Provider<GetAllNotesUseCase> getAllNotesProvider,
      Provider<CreateNoteUseCase> createNoteProvider, Provider<SaveNoteUseCase> saveNoteProvider,
      Provider<AutoSaveNoteUseCase> autoSaveProvider,
      Provider<DeleteNoteUseCase> deleteNoteProvider) {
    return new NoteEditorViewModel_Factory(getAllNotesProvider, createNoteProvider, saveNoteProvider, autoSaveProvider, deleteNoteProvider);
  }

  public static NoteEditorViewModel newInstance(GetAllNotesUseCase getAllNotes,
      CreateNoteUseCase createNote, SaveNoteUseCase saveNote, AutoSaveNoteUseCase autoSave,
      DeleteNoteUseCase deleteNote) {
    return new NoteEditorViewModel(getAllNotes, createNote, saveNote, autoSave, deleteNote);
  }
}
