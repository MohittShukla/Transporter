package com.freightfox.transporter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for Swagger UI documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transporter Assignment API")
                        .version("1.0.0")
                        .description("API for optimizing transporter-to-lane assignments. "
                                + "Submit lane and transporter data, then get cost-minimized "
                                + "assignments within a specified transporter limit.")
                        .contact(new Contact()
                                .name("Mohit Shukla")
                                .url("https://github.com/MohittShukla")));
    }
}
