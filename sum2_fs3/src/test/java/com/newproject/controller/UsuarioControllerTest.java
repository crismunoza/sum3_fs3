package com.newproject.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.Authentication;
import com.newproject.model.Role;
import com.newproject.model.Usuario;
import com.newproject.service.UsuarioService;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private Authentication mockAuth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarUsuario_UsernameExists() {
        Usuario usuario = new Usuario();
        usuario.setUsername("existingUser");
        when(usuarioService.existePorUsername("existingUser")).thenReturn(true);

        ResponseEntity<?> response = usuarioController.registrarUsuario(usuario);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El nombre de usuario ya está en uso.", response.getBody());
    }

    @Test
    void testRegistrarUsuario_EmailExists() {
        Usuario usuario = new Usuario();
        usuario.setEmail("existingEmail@example.com");
        when(usuarioService.existePorEmail("existingEmail@example.com")).thenReturn(true);

        ResponseEntity<?> response = usuarioController.registrarUsuario(usuario);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El correo electrónico ya está en uso.", response.getBody());
    }

    @Test
    void testRegistrarUsuario_Success() {
        Usuario usuario = new Usuario();
        usuario.setUsername("newUser");
        usuario.setEmail("newEmail@example.com");
        when(usuarioService.existePorUsername("newUser")).thenReturn(false);
        when(usuarioService.existePorEmail("newEmail@example.com")).thenReturn(false);
        when(usuarioService.registrarUsuario(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<?> response = usuarioController.registrarUsuario(usuario);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testObtenerTodosLosUsuarios() {
        List<Usuario> usuarios = Arrays.asList(new Usuario(), new Usuario());
        when(usuarioService.obtenerTodosLosUsuarios()).thenReturn(usuarios);

        ResponseEntity<List<Usuario>> response = usuarioController.obtenerTodosLosUsuarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarios, response.getBody());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testBuscarUsuarioPorUsername_Success() {
        Usuario usuario = new Usuario();
        usuario.setUsername("user");
        when(usuarioService.buscarPorUsername("user")).thenReturn(Optional.of(usuario));
        when(mockAuth.getName()).thenReturn("user");
        when(mockAuth.getAuthorities()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = usuarioController.buscarUsuarioPorUsername("user", mockAuth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }


    @Test
    @WithMockUser(roles = "USER")
    void testBuscarUsuarioPorUsername_Forbidden() {
        Usuario usuario = new Usuario();
        usuario.setUsername("user");
        when(usuarioService.buscarPorUsername("user")).thenReturn(Optional.of(usuario));
        when(mockAuth.getName()).thenReturn("anotherUser");

        ResponseEntity<?> response = usuarioController.buscarUsuarioPorUsername("user", mockAuth);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permiso para acceder a esta información.", response.getBody());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testActualizarUsuario_Success() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user");
        when(usuarioService.buscarPorUsername("user")).thenReturn(Optional.of(usuario));
        when(mockAuth.getName()).thenReturn("user");
        when(usuarioService.actualizarUsuario(any(Long.class), any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<?> response = usuarioController.actualizarUsuario(1L, usuario, mockAuth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuario, response.getBody());
    }

    @Test
    @WithMockUser(roles = "USER")  // El username debe coincidir con el mock
    void testActualizarUsuario_Forbidden() {
        // Crear el usuario mockeado (usuario que se intenta actualizar)
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("user1");

        // Crear el usuario que está autenticado (usuario que realiza la acción)
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("anotherUser");

        // Configurar el mock para el servicio
        when(usuarioService.buscarPorUsername("user1")).thenReturn(Optional.of(usuario1)); // Usuario que intenta ser actualizado
        when(usuarioService.buscarPorUsername("anotherUser")).thenReturn(Optional.of(usuario2)); // Usuario autenticado

        // Simular la autenticación de un usuario diferente
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("anotherUser"); // Usuario diferente
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList()); // Sin privilegios de ADMIN

        // Realizar la llamada al controlador
        ResponseEntity<?> response = usuarioController.actualizarUsuario(1L, usuario1, authentication);

        // Verificar el resultado esperado
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permiso para modificar este usuario.", response.getBody());
    }

    

    @Test
    @WithMockUser(roles = "USER")
    void testEliminarUsuario_Success() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user");
        usuario.setApellido("user");
        usuario.setEmail("user@example.com");
        usuario.setPassword( "$2a$10$1v6Zz1Zz9Zz9Zz9Zz9Zz9O");
        usuario.setRol(Role.ROLE_USER);
        when(usuarioService.buscarPorUsername("user")).thenReturn(Optional.of(usuario));
        when(mockAuth.getName()).thenReturn("user");

        ResponseEntity<?> response = usuarioController.eliminarUsuario(1L, mockAuth);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testEliminarUsuario_Forbidden() {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setUsername("user1");
        usuario1.setApellido("Apellido1");
        usuario1.setEmail("user1@example.com");
        usuario1.setPassword("$2a$10$1v6Zz1Zz9Zz9Zz9Zz9Zz9O");
        usuario1.setRol(Role.ROLE_USER);
    
        // Crear el usuario 2 (usuario objetivo de la eliminación)
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setUsername("user2");
        usuario2.setApellido("Apellido2");
        usuario2.setEmail("user2@example.com");
        usuario2.setPassword("$2a$10$1v6Zz1Zz9Zz9Zz9Zz9Zz9O");
        usuario2.setRol(Role.ROLE_USER);
        when(usuarioService.buscarPorUsername("user1")).thenReturn(Optional.of(usuario1));
        when(mockAuth.getAuthorities()).thenReturn(Collections.emptyList());
        when(mockAuth.getName()).thenReturn("user1");  

       ResponseEntity<?> response = usuarioController.eliminarUsuario(2L, mockAuth);  

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permiso para eliminar este usuario.", response.getBody());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testEliminarUsuario_AdminSuccess() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("user");
        when(usuarioService.buscarPorUsername("admin")).thenReturn(Optional.of(usuario));
        when(mockAuth.getName()).thenReturn("admin");

        ResponseEntity<?> response = usuarioController.eliminarUsuario(1L, mockAuth);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}