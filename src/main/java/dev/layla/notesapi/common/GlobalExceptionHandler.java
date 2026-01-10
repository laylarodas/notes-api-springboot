package dev.layla.notesapi.common;

import dev.layla.notesapi.note.exception.NoteNotFoundException;
import dev.layla.notesapi.user.exception.UserNotFoundException;
import dev.layla.notesapi.note.exception.NoteAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Record para errores simples (un solo mensaje)
     */
    public record ApiError(int status, String message, LocalDateTime timestamp) {}

    /**
     * Record para errores de validación (múltiples campos)
     */
    public record ValidationError(int status, String message, List<FieldError> errors, LocalDateTime timestamp) {}
    
    public record FieldError(String field, String message) {}

    // ========== ERRORES DE VALIDACIÓN ==========

    /**
     * Captura errores de validación de @Valid (campos faltantes, formatos inválidos, etc.)
     * Devuelve una lista detallada de qué campos fallaron y por qué.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ValidationError body = new ValidationError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ========== ERRORES DE NEGOCIO ==========

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ApiError> handleNoteNotFound(NoteNotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        ApiError body = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(NoteAccessDeniedException.class)
    public ResponseEntity<ApiError> handleNoteAccessDenied(NoteAccessDeniedException ex) {
        ApiError body = new ApiError(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ========== ERROR GENÉRICO ==========

    /**
     * Captura cualquier excepción no controlada.
     * Evita exponer stacktraces al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError body = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        // Log del error real para debugging (en producción usarías un logger)
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
