package com.dkp.auth;

import com.dkp.auth.dto.AuthResponse;
import com.dkp.auth.dto.LoginRequest;
import com.dkp.auth.dto.RegisterRequest;
import com.dkp.auth.entity.User;
import com.dkp.auth.exception.InvalidCredentialsException;
import com.dkp.auth.exception.UserAlreadyExistsException;
import com.dkp.auth.repository.UserRepository;
import com.dkp.auth.security.JwtUtils;
import com.dkp.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User(1L, "Jyothika", "jyothika@example.com", "encodedPassword123", "STUDENT");
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("Jyothika", "jyothika@example.com", "Password@123", "STUDENT");

        when(userRepository.existsByEmail("jyothika@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtUtils.generateToken(eq(1L), eq("jyothika@example.com"), eq("Jyothika"), eq("STUDENT")))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("jyothika@example.com", response.getEmail());
        assertEquals("STUDENT", response.getRole());
        assertEquals("User registered successfully", response.getMessage());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateEmail_ThrowsException() {
        RegisterRequest request = new RegisterRequest("Jyothika", "jyothika@example.com", "Password@123", "STUDENT");

        when(userRepository.existsByEmail("jyothika@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("jyothika@example.com", "Password@123");

        when(userRepository.findByEmail("jyothika@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("Password@123", "encodedPassword123")).thenReturn(true);
        when(jwtUtils.generateToken(eq(1L), eq("jyothika@example.com"), eq("Jyothika"), eq("STUDENT")))
                .thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("jyothika@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void testLogin_WrongPassword_ThrowsException() {
        LoginRequest request = new LoginRequest("jyothika@example.com", "WrongPassword");

        when(userRepository.findByEmail("jyothika@example.com")).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", "encodedPassword123")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void testLogin_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest("unknown@example.com", "Password@123");

        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
