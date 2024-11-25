package com.newproject.controller;


import com.newproject.model.Usuario;
import com.newproject.security.JwtUtil;
import com.newproject.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthController(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Usuario usuario) {
        Optional<Usuario> optionalUsuario = usuarioService.buscarPorUsername(usuario.getUsername());

        if (optionalUsuario.isEmpty()) {
            logger.error("Usuario no encontrado: {}", usuario.getUsername());
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuarioDB = optionalUsuario.get();

        if (!passwordEncoder.matches(usuario.getPassword(), usuarioDB.getPassword())) {
            logger.warn("Contraseña incorrecta para el usuario: {}", usuario.getUsername());
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        String jwt = jwtUtil.generateToken(usuarioDB.getUsername(), usuarioDB.getRol().name());

        logger.info("Login exitoso para el usuario: {}", usuario.getUsername());
        logger.info("Rol del usuario {}: {}", usuario.getUsername(), usuarioDB.getRol().name());

        return ResponseEntity.ok(jwt);
    }
}
