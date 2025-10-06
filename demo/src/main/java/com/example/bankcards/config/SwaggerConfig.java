package com.example.bankcards.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Cards API")
                        .version("1.0")
                        .description("API для управления банковскими картами")
                        .contact(new Contact()
                                .name("Support")
                                .email("support@bank.com")))
                 .externalDocs(new ExternalDocumentation()
                .description("Bank Cards Documentation")
                .url("docs/openapi.yaml"));
    }
}
