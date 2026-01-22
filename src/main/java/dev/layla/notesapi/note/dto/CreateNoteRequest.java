package dev.layla.notesapi.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear una nota.
 * El userId ya no es necesario porque se obtiene del token JWT.
 */
public record CreateNoteRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        String content
) {}
