package com.newproject.service;

import com.newproject.model.Role;
import com.newproject.model.Usuario;
import com.newproject.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("John");
        usuario.setApellido("Doe");
        usuario.setUsername("johndoe");
        usuario.setPassword("password");
        usuario.setEmail("johndoe@example.com");
        usuario.setRol(Role.ROLE_USER);
    }

    @Test
    void testRegistrarUsuario() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.registrarUsuario(usuario);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void testBuscarPorUsername() {
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.buscarPorUsername("johndoe");

        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
    }

    @Test
    void testBuscarPorEmail() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.buscarPorEmail("johndoe@example.com");

        assertNotNull(result);
        assertEquals("johndoe@example.com", result.getEmail());
    }

    @Test
    void testExistePorUsername() {
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(true);

        Boolean result = usuarioService.existePorUsername("johndoe");

        assertTrue(result);
    }

    @Test
    void testExistePorEmail() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        Boolean result = usuarioService.existePorEmail("johndoe@example.com");

        assertTrue(result);
    }

    @Test
    void testObtenerTodosLosUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        List<Usuario> result = usuarioService.obtenerTodosLosUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testEsAdmin() {
        usuario.setRol(Role.ROLE_ADMIN);
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.of(usuario));

        boolean result = usuarioService.esAdmin("johndoe");

        assertTrue(result);
    }

    @Test
    void testActualizarUsuario() {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setUsername("newusername");
        usuarioActualizado.setPassword("newpassword");
        usuarioActualizado.setEmail("newemail@example.com");
        usuarioActualizado.setNombre("NewName");
        usuarioActualizado.setApellido("NewLastName");
        usuarioActualizado.setRol(Role.ROLE_USER);

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.actualizarUsuario(1L, usuarioActualizado);

        assertNotNull(result);
        assertEquals("newusername", result.getUsername());
        assertEquals("encodedNewPassword", result.getPassword());
        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("NewName", result.getNombre());
        assertEquals("NewLastName", result.getApellido());
        assertEquals(Role.ROLE_USER, result.getRol());
    }

    @Test
    void testEliminarUsuario() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }
}