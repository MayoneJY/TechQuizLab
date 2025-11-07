package com.mayonedev.battle.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Battle API")
                    .description("Battle Service API Documentation")
                    .version("v1.0.0"));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        String[] paths = { "/api/**" };
        String[] packagesToScan = { "com.mayonedev.battle.controller" };
        return GroupedOpenApi.builder()
                .group("Battle API v1")
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .build();
    }
}