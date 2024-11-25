package com.newproject.service;

import com.newproject.model.Usuario;
import com.newproject.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

  
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Iniciando la búsqueda del usuario con username: {}", username);        
        // Buscar el usuario por su nombre de usuario
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        // Si no se encuentra el usuario, lanzar una excepción
        if (usuario.isEmpty()) {
            logger.error("Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        // Si se encuentra el usuario, construir los detalles del usuario
        Usuario foundUser = usuario.get();
        logger.debug("Usuario encontrado: {} con rol: {}", foundUser.getUsername(), foundUser.getRol());

       // Construir los roles del usuario
        String[] roles = { foundUser.getRol().name() }; 
       // Construir los detalles del usuario
        UserDetails userDetails = User
                .withUsername(foundUser.getUsername())
                .password(foundUser.getPassword())
                .authorities(roles) 
                .build();

        logger.info("Detalles del usuario construidos correctamente para: {}", username);
        return userDetails;
    }
}
