package vallegrande.edu.pe.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * CorsConfig - Configuración global de CORS para WebFlux.
 * NOTA: La configuración principal de CORS se hace en SecurityConfig
 * usando CorsConfigurationSource, que tiene precedencia sobre este.
 * Este archivo actúa como fallback para rutas no cubiertas por Spring Security.
 */
@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")   // allowedOriginPatterns en lugar de allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
