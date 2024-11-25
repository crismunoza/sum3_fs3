package com.newproject.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.newproject.model.Role;
import com.newproject.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<Usuario> findByRol(Role rol);
}
