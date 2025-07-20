package com.example.restaurant.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.restaurant.entidades.Usuario;
import com.example.restaurant.entidades.Rol;
import com.example.restaurant.repositorios.UsuarioRepository;

@Controller
public class UsuarioController {
    
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        return "registro-usuario";
    }
    
    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombreUsuario,
                                @RequestParam String contrasena,
                                Model model) {
        
        // Verificar si el usuario ya existe
        if (usuarioRepository.findByNombreUsuario(nombreUsuario) != null) {
            model.addAttribute("error", "El nombre de usuario ya está registrado");
            return "registro-usuario";
        }
        
        // Crear y guardar el nuevo usuario con Rol.USER por defecto
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreUsuario(nombreUsuario);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setRol(Rol.USER); // Asignamos el Enum USER
        
        usuarioRepository.save(nuevoUsuario);
        
        model.addAttribute("mensaje", "Usuario registrado exitosamente como " + Rol.USER);
        return "registro-usuario";
    }
}