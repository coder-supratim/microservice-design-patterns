package com.kish.mcdp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server")
            ))
            .info(new Info()
                .title("Order Service API")
                .version("1.0.0")
                .description("REST API for managing orders in a microservice architecture. " +
                    "This service provides complete CRUD operations for orders with PostgreSQL persistence.")
                .contact(new Contact()
                    .name("Order Service Team")
                    .url("https://github.com/microservice-design-patterns"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}

