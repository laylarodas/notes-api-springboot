package dev.layla.notesapi.auth;

import dev.layla.notesapi.auth.dto.AuthResponse;
import dev.layla.notesapi.auth.dto.LoginRequest;
import dev.layla.notesapi.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticación.
 * Endpoints públicos (no requieren token).
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registra un nuevo usuario y retorna un token JWT.
     * 
     * POST /auth/register
     * {
     *   "name": "Layla",
     *   "email": "layla@example.com",
     *   "password": "123456"
     * }
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario y retorna un token JWT")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Autentica un usuario y retorna un token JWT.
     * 
     * POST /auth/login
     * {
     *   "email": "layla@example.com",
     *   "password": "123456"
     * }
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna un token JWT")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
