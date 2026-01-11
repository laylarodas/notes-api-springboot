package dev.layla.notesapi.auth.dto;

/**
 * Respuesta de autenticación que contiene el token JWT.
 */
public record AuthResponse(
        String token,
        long expiresIn
) {}
