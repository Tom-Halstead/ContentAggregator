package com.contentaggregator.controller;

import com.contentaggregator.response.AuthResponse;
import com.contentaggregator.service.AuthService;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<?> handleCognitoRedirect(@RequestParam String code, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.exchangeCodeForToken(code);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to authenticate.");
        }
    }
}
