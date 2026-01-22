package dev.layla.notesapi.config;

import dev.layla.notesapi.auth.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security con JWT.
 * 
 * Rutas públicas (no requieren token):
 * - /auth/** (login, registro)
 * - /actuator/** (health checks)
 * - /swagger-ui/**, /v3/api-docs/** (documentación)
 * - /h2-console/** (consola de BD en desarrollo)
 * 
 * Rutas protegidas (requieren token JWT válido):
 * - /notes/**
 * - /users/**
 * - Cualquier otra ruta
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF - no es necesario para APIs REST con JWT
            .csrf(csrf -> csrf.disable())
            
            // Permitir frames del mismo origen (necesario para H2 Console)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            
            // Configurar manejo de sesiones como STATELESS
            // El servidor NO guardará sesiones - toda la info viene en el JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configurar autorización de peticiones
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas - Autenticación
                .requestMatchers("/auth/**").permitAll()
                
                // Rutas públicas - Actuator (health checks)
                .requestMatchers("/actuator/**").permitAll()
                
                // Rutas públicas - Swagger/OpenAPI
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                
                // Rutas públicas - H2 Console (solo desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Ruta raíz pública
                .requestMatchers("/").permitAll()
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            
            // Agregar el filtro JWT ANTES del filtro de autenticación estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Bean para encriptar contraseñas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
