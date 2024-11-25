package com.newproject.controller;

import com.newproject.model.Usuario;
import com.newproject.security.JwtUtil;
import com.newproject.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import com.newproject.model.Role;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginUserNotFound() {
        Usuario usuario = new Usuario();
        usuario.setUsername("nonexistentUser");

        when(usuarioService.buscarPorUsername(anyString())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(usuario);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void testLoginIncorrectPassword() {
        Usuario usuario = new Usuario();
        usuario.setUsername("existingUser");
        usuario.setPassword("wrongPassword");

        Usuario usuarioDB = new Usuario();
        usuarioDB.setUsername("existingUser");
        usuarioDB.setPassword("correctEncodedPassword");

        when(usuarioService.buscarPorUsername(anyString())).thenReturn(Optional.of(usuarioDB));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseEntity<?> response = authController.login(usuario);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Contraseña incorrecta", response.getBody());
    }

    @Test
    void testLoginSuccess() {
        Usuario usuario = new Usuario();
        usuario.setUsername("existingUser");
        usuario.setPassword("correctPassword");

        Usuario usuarioDB = new Usuario();
        usuarioDB.setUsername("existingUser");
        usuarioDB.setPassword("correctEncodedPassword");
        usuarioDB.setRol(Role.ROLE_USER);

        when(usuarioService.buscarPorUsername(anyString())).thenReturn(Optional.of(usuarioDB));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mockJwtToken");

        ResponseEntity<?> response = authController.login(usuario);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("mockJwtToken", response.getBody());
    }
}