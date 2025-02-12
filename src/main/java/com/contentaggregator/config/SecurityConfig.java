
package com.contentaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight CORS requests
                        .requestMatchers("/", "/index.html", "/user-info").permitAll() // Public routes
                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://127.0.0.1:5500/src/main/resources/static/index.html", true) // ✅ Redirects to frontend after login
                        .failureUrl("http://127.0.0.1:5500/index.html?error=true") // Redirect on failure
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("http://127.0.0.1:5500/src/main/resources/static/index.html") // ✅ Redirects to frontend after logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // ✅ Ensures session-based authentication
                );

        return http.build();
    }

    // Optional: Customize JWT Authentication Converter
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // You can customize claims here (for example, extract roles or other details)
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:5500", "http://127.0.0.1:8080")); // Add frontend domain(s)
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // Apply CORS globally
        return source;
    }
}

