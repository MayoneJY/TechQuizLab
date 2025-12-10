package com.mayonedev.battle.config;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestLoggingConfig {

    @Bean
    public RestClientCustomizer logCustomizer() {
        return builder -> builder.requestInterceptor((request, body, execution) -> {

            System.out.println("===== Spring AI Outbound Request =====");
            System.out.println("URL: " + request.getURI());
            System.out.println("Method: " + request.getMethod());
            System.out.println("Headers: " + request.getHeaders());
            System.out.println("======================================");

            return execution.execute(request, body);
        });
    }
}
