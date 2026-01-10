package dev.layla.notesapi.note.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Note.
 * Usando record de Java 17+ para inmutabilidad y menos código.
 */
public record NoteResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        boolean archived
) {}
