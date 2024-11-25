package com.newproject.controller;


import com.newproject.model.Usuario;
import com.newproject.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;
    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private static final String ROLE_ADMIN = "ROLE_ADMIN"; 
    private static final String ROLE_USER = "ROLE_USER"; 

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registrarUsuario(@Valid @RequestBody Usuario usuario) {

        if (Boolean.TRUE.equals(usuarioService.existePorUsername(usuario.getUsername()))) {
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya está en uso.");
        }
        if (Boolean.TRUE.equals(usuarioService.existePorEmail(usuario.getEmail()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está en uso.");
        }
        
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PreAuthorize("hasRole('" + ROLE_ADMIN + "')")
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosLosUsuarios() {
        logger.info("Intento de acceso al método obtenerTodosLosUsuarios: acceso permitido solo para ROLE_ADMIN");

        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "') or hasRole('" + ROLE_ADMIN + "')")
    @GetMapping("/{username}")
    public ResponseEntity<Object> buscarUsuarioPorUsername(@PathVariable String username, Authentication authentication) {
        Optional<Usuario> usuario = usuarioService.buscarPorUsername(username);

        if (usuario.isPresent()) {
            if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN)) &&
                    !authentication.getName().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permiso para acceder a esta información.");
            }
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NO_ENCONTRADO);
        }
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "') or hasRole('" + ROLE_ADMIN + "')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuarioActualizado,
            Authentication authentication) {
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorUsername(authentication.getName());
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NO_ENCONTRADO);
        }
        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN)) &&
                !usuarioExistente.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para modificar este usuario.");
        }

        Usuario usuario = usuarioService.actualizarUsuario(id, usuarioActualizado);
        return ResponseEntity.ok(usuario);
    }

    @PreAuthorize("hasRole('" + ROLE_USER + "') or hasRole('" + ROLE_ADMIN + "')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarUsuario(@PathVariable Long id, Authentication authentication) {
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorUsername(authentication.getName());
        if (usuarioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USUARIO_NO_ENCONTRADO);
        }
        if (authentication.getAuthorities().stream().noneMatch(auth -> auth.getAuthority().equals(ROLE_ADMIN)) &&
                !usuarioExistente.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este usuario.");
        }

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
