package dev.layla.notesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security.
 * 
 * IMPORTANTE: Esta es una configuración TEMPORAL que permite todas las peticiones.
 * La iremos modificando paso a paso para agregar autenticación JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     * 
     * Por ahora:
     * - Deshabilitamos CSRF (no necesario en APIs stateless con JWT)
     * - Configuramos sesiones como STATELESS (no guardamos estado en servidor)
     * - Permitimos TODAS las peticiones (temporal, lo cambiaremos después)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF - no es necesario para APIs REST con JWT
            // CSRF protege contra ataques en aplicaciones con cookies/sesiones
            .csrf(csrf -> csrf.disable())
            
            // Configurar manejo de sesiones como STATELESS
            // El servidor NO guardará sesiones - toda la info viene en el JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar autorización de peticiones
            // TEMPORAL: Permitimos todo. Después protegeremos rutas específicas.
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        
        return http.build();
    }

    /**
     * Bean para encriptar contraseñas con BCrypt.
     * 
     * BCrypt es un algoritmo de hashing diseñado para passwords:
     * - Incluye "salt" automático (protege contra rainbow tables)
     * - Es lento a propósito (dificulta ataques de fuerza bruta)
     * - El factor de coste (strength) se puede ajustar
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
