package dev.layla.notesapi.note.exception;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Note with id " + id + " was not found");
    }
}
