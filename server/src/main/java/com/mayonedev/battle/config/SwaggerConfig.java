package com.mayonedev.battle.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
                SecurityScheme securityScheme = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization");

                SecurityRequirement securityRequirement = new SecurityRequirement()
                                .addList("Authorization");

                return new OpenAPI()
                                .addServersItem(new Server().url("https://battle.mayonedev.com")
                                                .description("Default Server"))
                                .info(new Info()
                                                .title("Battle API")
                                                .description("Battle Service API Documentation")
                                                .version("v1.0.0"))
                                .components(new Components()
                                                .addSecuritySchemes("Authorization", securityScheme))
                                .addSecurityItem(securityRequirement);
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