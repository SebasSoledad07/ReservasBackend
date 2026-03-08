package com.devsenior.soledad.reservas_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the application.
 * <p>
 * By default allows requests from http://localhost:4200 and http://localhost:3000. Adjust the allowedOrigins
 * if your frontend runs on a different origin.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure cross-origin requests processing.
     *
     * @param registry the CorsRegistry used to add mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
