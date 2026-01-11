package dev.layla.notesapi.auth;

import dev.layla.notesapi.auth.dto.AuthResponse;
import dev.layla.notesapi.auth.dto.LoginRequest;
import dev.layla.notesapi.auth.dto.RegisterRequest;
import dev.layla.notesapi.auth.exception.EmailAlreadyExistsException;
import dev.layla.notesapi.auth.exception.InvalidCredentialsException;
import dev.layla.notesapi.user.User;
import dev.layla.notesapi.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación.
 * Maneja el registro de usuarios y el login.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registra un nuevo usuario.
     * 
     * 1. Verifica que el email no exista
     * 2. Hashea el password
     * 3. Guarda el usuario
     * 4. Genera y retorna el token JWT
     */
    public AuthResponse register(RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException(request.email());
        }

        // Crear usuario con password hasheado
        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        // Guardar en BD
        userRepository.save(user);

        // Generar token
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, jwtService.getExpirationTime());
    }

    /**
     * Autentica un usuario existente.
     * 
     * 1. Busca el usuario por email
     * 2. Verifica el password
     * 3. Genera y retorna el token JWT
     */
    public AuthResponse login(LoginRequest request) {
        // Buscar usuario por email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        // Verificar password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Generar token
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, jwtService.getExpirationTime());
    }
}
