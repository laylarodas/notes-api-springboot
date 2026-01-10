package dev.layla.notesapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un usuario (registro).
 */
public record CreateUserRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        @Size(max = 150, message = "email must be at most 150 characters")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 100, message = "password must be between 6 and 100 characters")
        String password
) {}
