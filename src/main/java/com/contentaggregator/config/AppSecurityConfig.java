package com.contentaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig implements WebMvcConfigurer {

    // ✅ Cognito Properties
    @Value("${CONTENT_AGGREGATOR_COGNITO_CLIENT_ID}")
    private String clientId;

    @Value("${CONTENT_AGGREGATOR_COGNITO_SECRET_KEY}")
    private String secretKey;

    @Value("${spring.security.oauth2.client.provider.cognito.issuer-uri}")
    private String issuerUrl;

    @Value("${spring.security.oauth2.client.registration.cognito.redirect-uri}")
    private String redirectUri;

    @Value("${aws.cognito.domain}")
    private String domain;

    private String scope = "email openid";

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return secretKey;
    }

    public String getIssuerUrl() {
        return issuerUrl;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTokenEndpoint() {
        return this.issuerUrl + "/oauth2/token";
    }

    // ✅ Provide RestTemplate bean
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * ✅ JWT Authentication Converter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    /**
     * ✅ OAuth2 Client Services
     */
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    /**
     * ✅ Configures security filters including OAuth2 login
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2AuthorizedClientRepository authorizedClientRepository) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/index.html", "/post-login", "/user-info", "/login/oauth2/code/**", "/login-failed").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizedClientRepository(authorizedClientRepository)
                        .defaultSuccessUrl("/api/auth/post-login", true)
                        .failureUrl("/login-failed")
                )
                .sessionManagement(session -> session
                        .sessionFixation().none() // ✅ Prevent session ID change after login
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ✅ Ensure session is maintained
                );

        return http.build();
    }

    /**
     * ✅ CORS Configuration (Ensures Frontend Access)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://127.0.0.1:8080",
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:8080"
        )); // Add frontend domains
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    /**
     * ✅ MVC View Controllers
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }

    /**
     * ✅ CORS Configuration for MVC (Alternative for non-Spring Security routes)
     */
    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:8080", "http://127.0.0.1:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

