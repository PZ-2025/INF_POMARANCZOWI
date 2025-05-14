package com.orange.bookmanagment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguracja Spring MVC dla aplikacji.
 * <p>
 * Obsługuje udostępnianie zasobów statycznych
 * oraz mapowanie reguł CORS (Cross-Origin Resource Sharing).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Rejestruje obsługę zasobów statycznych w ścieżce `/uploads/**`,
     * umożliwiając dostęp do przesłanych plików z folderu lokalnego `uploads/`.
     *
     * @param registry rejestr zasobów
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600)
                .resourceChain(true);
    }

    /**
     * Konfiguruje reguły CORS dla ścieżki `/uploads/**`,
     * umożliwiając dostęp z klienta działającego na `http://localhost:4200`.
     *
     * @param registry rejestr reguł CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
