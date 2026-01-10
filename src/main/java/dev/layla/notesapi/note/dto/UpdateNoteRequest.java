package dev.layla.notesapi.note.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar una nota.
 * Todos los campos son opcionales (PATCH-like update).
 */
public record UpdateNoteRequest(
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        String content,

        Boolean archived
) {}
