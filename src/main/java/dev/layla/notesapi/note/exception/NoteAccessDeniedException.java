package dev.layla.notesapi.note.exception;

public class NoteAccessDeniedException extends RuntimeException {
    public NoteAccessDeniedException(Long noteId, Long userId) {
        super("User " + userId + " is not allowed to access note " + noteId);
    }
}
