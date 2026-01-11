package dev.layla.notesapi.auth.exception;

/**
 * Excepción lanzada cuando las credenciales de login son inválidas.
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
