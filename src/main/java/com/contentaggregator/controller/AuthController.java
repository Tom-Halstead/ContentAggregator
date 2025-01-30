package com.contentaggregator.controller;

import com.contentaggregator.exception.InvalidCodeException;
import com.contentaggregator.response.AuthResponse;
import com.contentaggregator.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String code, HttpServletResponse response) {
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization code is required.");
        }

        try {
            AuthResponse authResponse = authService.exchangeCodeForToken(code);
            // Consider setting HttpOnly cookies or similar if managing sessions
            return ResponseEntity.ok(authResponse);
        } catch (InvalidCodeException ice) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ice.getMessage());
        } catch (AuthenticationException ae) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed.");
        } catch (Exception e) {
            // Generic exception handler as a fallback
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

}
