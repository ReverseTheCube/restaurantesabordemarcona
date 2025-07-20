package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Query automática por nombre de método
	Usuario findByNombreUsuarioAndContrasena(String nombreUsuario, String contrasena);

	Usuario findByNombreUsuario(String nombreUsuario);

}