package com.dkp.auth.controller;

import com.dkp.auth.dto.AuthResponse;
import com.dkp.auth.dto.LoginRequest;
import com.dkp.auth.dto.RegisterRequest;
import com.dkp.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "https://digital-knowledge-platform-is9l.onrender.com")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to register a new user.
     * Accessible publicly.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to authenticate an existing user.
     * Returns JWT upon successful credentials verification.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Helper endpoint to validate a JWT token.
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(
            @RequestParam String token) {

        boolean valid = authService.validateToken(token);

        return ResponseEntity.ok(Map.of(
                "valid", valid,
                "token", token
        ));
    }
}