package dev.layla.notesapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger con autenticación JWT.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notes API")
                        .description("""
                                API REST para gestionar notas personales con autenticación JWT.
                                
                                ## Autenticación
                                1. Regístrate en `/auth/register` o inicia sesión en `/auth/login`
                                2. Copia el token JWT de la respuesta
                                3. Haz clic en "Authorize" y pega el token (sin "Bearer ")
                                
                                ## Endpoints protegidos
                                Todos los endpoints de `/notes` requieren autenticación.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Layla")
                                .email("layla@example.com")
                                .url("https://github.com/laylarodas"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo")
                ))
                // Configuración de seguridad JWT para Swagger UI
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Introduce tu token JWT (sin 'Bearer ')")
                        )
                );
    }
}
