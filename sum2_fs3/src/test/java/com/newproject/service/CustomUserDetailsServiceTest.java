package com.newproject.service;

import com.newproject.model.Role;
import com.newproject.model.Usuario;
import com.newproject.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserFound() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setUsername("testUser");
        usuario.setPassword("testPassword");
        usuario.setRol(Role.ROLE_USER);
        when(usuarioRepository.findByUsername("testUser")).thenReturn(Optional.of(usuario));

        // Act
        var userDetails = customUserDetailsService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("testPassword", userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().isEmpty());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        // Arrange
        when(usuarioRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonExistentUser");
        });
    }
}