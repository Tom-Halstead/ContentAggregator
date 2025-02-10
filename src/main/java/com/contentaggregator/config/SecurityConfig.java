package com.contentaggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CognitoLogoutHandler cognitoLogoutHandler = new CognitoLogoutHandler();

        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Explicitly allow preflight requests
                        .requestMatchers("/").permitAll() // You can also customize more endpoints
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // You can specify a custom login page
                        .clientRegistrationRepository(clientRegistrationRepository()) // Register OAuth2 client
                        .authorizedClientRepository(authorizedClientRepository()) // Manage OAuth2 clients
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(cognitoLogoutHandler)
                        .logoutUrl("/logout") // Customize logout URL if needed
                );

        return http.build();
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("cognito")
                .clientId("5oncoq9mddhbmluooq6kpib2kj")
                .clientSecret("your-client-secret")
                .authorizationUri("https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/oauth2/authorize")
                .tokenUri("https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/oauth2/token")
                .userInfoUri("https://us-east-29qbfa8ryf.auth.us-east-2.amazoncognito.com/oauth2/userInfo")
                .clientAuthenticationMethod("basic")  // Directly using string authentication
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/auth/callback")
                .scope("openid", "email")
                .build();

        return new InMemoryClientRegistrationRepository(registration);
    }

    // Define an AuthorizedClientRepository if needed
    private OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository(); // Store authorized clients in session
    }
}