package dev.layla.notesapi.auth.exception;

/**
 * Excepción lanzada cuando se intenta registrar un email que ya existe.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}
