package com.oauth2flows.authorizationcodeflow.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                        .csrf(csrf -> csrf.disable())
                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                        .authorizeHttpRequests(authorize -> authorize
                                    .requestMatchers("/", "/index.html", "/static/**", "/login", "/error").permitAll()
                                    .requestMatchers("/api/user").authenticated()
                                    .anyRequest().authenticated())
                        .oauth2Login(oauth2 -> oauth2
                                    // Redirect to frontend after successful login
                                    .defaultSuccessUrl("http://localhost:3000", true))
                        .logout(logout -> logout
                                    .logoutSuccessUrl("http://localhost:3000")
                                    .invalidateHttpSession(true)
                                    .deleteCookies("JSESSIONID"));

            return http.build();
      }

      // CORS configuration
      @Bean
      public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("*"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
      }
}