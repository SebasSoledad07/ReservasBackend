package com.devsenior.soledad.reservas_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for configuring global web settings.
 *
 * <p>
 * This configuration enables Cross-Origin Resource Sharing (CORS) to allow
 * the frontend application to access the backend API when running on a
 * different domain or port.
 * </p>
 *
 * <p>
 * In production, allowed origins should be restricted to the frontend
 * domain instead of allowing all origins.
 * </p>
 */
@Configuration
public class WebConfig {

    /**
     * Configures global CORS mappings for the application.
     *
     * @return a {@link WebMvcConfigurer} that defines CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            /**
             * Adds CORS mappings for API endpoints.
             *
             * @param registry the {@link CorsRegistry} used to configure CORS rules
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                        .allowedOrigins(
                                "*"
                        )
                        .allowedMethods(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "PATCH",
                                "OPTIONS"
                        )
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}