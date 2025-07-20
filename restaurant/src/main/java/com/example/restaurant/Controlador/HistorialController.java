package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.repositorios.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin(origins = "*")
public class HistorialController {

    @Autowired
    private PedidoRepository pedidoRepo;

    @GetMapping
    public List<Pedido> getHistorial(HttpSession session) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        return pedidoRepo.findByUsuario_IdUsuario(usuarioId);
    }
}