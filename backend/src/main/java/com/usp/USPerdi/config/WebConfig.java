package com.usp.USPerdi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads-dir:uploads}")
    private String uploadsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String caminhoUpload = Paths.get(uploadsDir).toAbsolutePath().normalize().toUri().toString();

        // Mapeia a URL pública para a pasta física no servidor
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(caminhoUpload);
    }
}