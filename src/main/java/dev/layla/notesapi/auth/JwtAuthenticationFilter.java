package dev.layla.notesapi.auth;

import dev.layla.notesapi.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta cada petición HTTP.
 * 
 * Flujo:
 * 1. Extrae el token del header "Authorization: Bearer <token>"
 * 2. Valida el token usando JwtService
 * 3. Carga el usuario de la BD
 * 4. Configura el SecurityContext con el usuario autenticado
 * 
 * Este filtro se ejecuta ANTES de que la petición llegue al controlador.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Obtener el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token (quitar "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // 4. Extraer el email/username del token
            final String userEmail = jwtService.extractUsername(jwt);

            // 5. Si tenemos email y NO hay autenticación previa en el contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Buscar el usuario en la BD
                UserDetails userDetails = userRepository.findByEmail(userEmail)
                        .orElse(null);

                // 7. Si el usuario existe y el token es válido
                if (userDetails != null && jwtService.isTokenValid(jwt, userDetails)) {

                    // 8. Crear el objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // No necesitamos credenciales, ya validamos el token
                            userDetails.getAuthorities()
                    );

                    // 9. Agregar detalles de la petición
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 10. Establecer la autenticación en el SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido o expirado - simplemente no autenticamos
            // La petición continuará y Spring Security denegará acceso a rutas protegidas
            logger.debug("JWT validation failed: " + e.getMessage());
        }

        // 11. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
