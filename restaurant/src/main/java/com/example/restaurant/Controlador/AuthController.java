package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Usuario;
import com.example.restaurant.repositorios.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor (práctica recomendada)
    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // DTO simple para recibir los datos del login de forma segura
    public static class LoginRequest {
        public String nombreUsuario;
        public String contrasena;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("Intento de login para: " + loginRequest.nombreUsuario);

        Usuario encontrado = usuarioRepository.findByNombreUsuario(loginRequest.nombreUsuario);

        // 1. Verificar si el usuario existe
        if (encontrado == null) {
            System.out.println("Resultado: Usuario no encontrado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuario o contraseña incorrectos"));
        }

        // 2. Verificar si la contraseña es correcta (¡IMPORTANTE!)
        // En una app real, las contraseñas deben estar encriptadas.
        if (!encontrado.getContrasena().equals(loginRequest.contrasena)) {
            System.out.println("Resultado: Contraseña incorrecta para el usuario " + loginRequest.nombreUsuario);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Usuario o contraseña incorrectos"));
        }

        System.out.println("Resultado: Login exitoso para " + encontrado.getNombreUsuario());

        // 3. Devolver una respuesta completa con los datos que el frontend necesita
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", encontrado.getIdUsuario());
        responseData.put("nombreUsuario", encontrado.getNombreUsuario());
        responseData.put("rol", encontrado.getRol().toString());

        return ResponseEntity.ok(responseData);
    }
}