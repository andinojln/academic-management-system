package com.academic.system.student.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://localhost:8080");
        gatewayServer.setDescription("Ruta a través del API Gateway");

        return new OpenAPI()
                .servers(List.of(gatewayServer))
                .info(new Info()
                        .title("API del Microservicio de Estudiantes")
                        .version("1.0")
                        .description("Documentación de los endpoints para la gestión " +
                                "de estudiantes del Sistema Académico.")
                );
    }
}
