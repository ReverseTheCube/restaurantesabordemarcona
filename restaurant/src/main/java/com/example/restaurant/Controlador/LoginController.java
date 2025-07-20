package com.example.restaurant.Controlador;

import org.springframework.web.bind.annotation.GetMapping;

public class LoginController {
	@GetMapping("/")
    public String login() {
        return "usuarios/login"; // Devuelve el nombre de la vista
    }
}
