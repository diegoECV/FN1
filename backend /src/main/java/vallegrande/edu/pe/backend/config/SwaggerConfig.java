package vallegrande.edu.pe.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:9090}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Visons S.A.C. — API REST")
                        .version("1.0.0")
                        .description("API transaccional para gestión de Agroexportación: " +
                                "Pedidos, Lotes, Envíos, Facturación, Trazabilidad y más.")
                        .contact(new Contact()
                                .name("Equipo Visons")
                                .email("dev@visons.pe"))
                        .license(new License().name("Privado").url("#")))
                .servers(List.of(
                        new Server().url("http://localhost:" + serverPort).description("Servidor local")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createBearerScheme()));
    }

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Ingresa el token JWT obtenido en /api/v1/auth/login");
    }
}
