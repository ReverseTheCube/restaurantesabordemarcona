package com.example.restaurant.Controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.restaurant.entidades.Usuario;
import com.example.restaurant.entidades.Rol;
import com.example.restaurant.repositorios.UsuarioRepository;

@Controller
@RequestMapping("/empleados")
public class EmpleadoController {
    
    private final UsuarioRepository usuarioRepository;
    
    public EmpleadoController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        System.out.println("Redirigiendo al formulario de registro estático");
        return "redirect:/registro-empleado.html";
    }
    
    @PostMapping("/registrar")
    public String registrarEmpleado(
            @RequestParam("nombreUsuario") String nombreUsuario,
            @RequestParam("contrasena") String contrasena,
            @RequestParam("confirmarContrasena") String confirmarContrasena,
            RedirectAttributes redirectAttributes) {
        System.out.println("Procesando registro de empleado: " + nombreUsuario);
        
        try {
            // Validar que las contraseñas coincidan
            if (!contrasena.equals(confirmarContrasena)) {
                redirectAttributes.addAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/registro-empleado.html";
            }
            
            // Validar campos no vacíos
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "El nombre de usuario es requerido");
                return "redirect:/registro-empleado.html";
            }
            
            if (contrasena == null || contrasena.trim().isEmpty()) {
                redirectAttributes.addAttribute("error", "La contraseña es requerida");
                return "redirect:/registro-empleado.html";
            }
            
            // Verificar si el usuario ya existe
            if (usuarioRepository.findByNombreUsuario(nombreUsuario.trim()) != null) {
                redirectAttributes.addAttribute("error", "El nombre de usuario ya está registrado");
                return "redirect:/registro-empleado.html";
            }
            
            // Crear nuevo empleado (Usuario)
            Usuario nuevoEmpleado = new Usuario();
            nuevoEmpleado.setNombreUsuario(nombreUsuario.trim());
            nuevoEmpleado.setContrasena(contrasena); // En producción, ¡debes encriptar esto!
            nuevoEmpleado.setRol(Rol.USER);
            
            // Guardar el empleado
            usuarioRepository.save(nuevoEmpleado);
            
            System.out.println("Empleado registrado exitosamente: " + nombreUsuario);
            redirectAttributes.addAttribute("success", "Empleado registrado exitosamente");
            
        } catch (Exception e) {
            System.out.println("Error al registrar empleado: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error al registrar empleado: " + e.getMessage());
        }
        
        return "redirect:/registro-empleado.html";
    }
}