package dev.layla.notesapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar un usuario.
 * Todos los campos son opcionales.
 */
public record UpdateUserRequest(
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @Email(message = "email must be a valid email address")
        @Size(max = 150, message = "email must be at most 150 characters")
        String email
) {}
