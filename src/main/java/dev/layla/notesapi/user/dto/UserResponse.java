package dev.layla.notesapi.user.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para User.
 * Usando record de Java 17+ para inmutabilidad y menos código.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt,
        int notesCount
) {}
