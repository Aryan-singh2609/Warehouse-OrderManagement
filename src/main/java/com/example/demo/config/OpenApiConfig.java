package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Warehouse Management API",
                version = "v1",
                description = "REST API for managing users, clients, fulfillment centers, products, orders, batches, and pickers.",
                contact = @Contact(name = "Warehouse Management Team"),
                license = @License(name = "Internal Use")
        ),
        servers = {
                @Server(url = "/", description = "Default server")
        }
)
@SecurityScheme(
        name = "sessionAuth",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "JSESSIONID",
        description = "Session-based authentication. Call /data/login first to establish the JSESSIONID cookie."
)
public class OpenApiConfig {
}
