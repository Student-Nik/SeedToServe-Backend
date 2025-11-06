package com.seedtoserve.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (for cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Allowed origins (include both localhost and production domains if any)
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // Allow common HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow headers used in authorization and custom requests
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");

        // Cache CORS preflight response for 1 hour
        config.setMaxAge(3600L);

        // Apply configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
