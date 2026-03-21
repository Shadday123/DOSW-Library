package edu.eci.dosw.DOSW_Library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DOSW Library API")
                        .version("1.0.0")
                        .description(
                                "API REST para la gestión de una biblioteca digital. " +
                                "Permite administrar libros, usuarios y préstamos con control " +
                                "de disponibilidad, multas por atraso (1000 pesos/día) y " +
                                "un límite de 5 préstamos activos por usuario."
                        )
                        .contact(new Contact()
                                .name("Equipo DOSW")
                                .email("dosw@eci.edu.co")
                        )
                        .license(new License()
                                .name("MIT License")
                        )
                );
    }
}
