package com.clientcredentialsflow.resourceserver.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Webconfig implements WebMvcConfigurer {

      @Override
      public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")
                        .allowCredentials(true)
                        .maxAge(3600); // 1 hour
      }
}