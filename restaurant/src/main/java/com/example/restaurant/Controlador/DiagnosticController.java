package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.repositorios.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// --- CORRECCIÓN AQUÍ: Quitamos el /api duplicado ---
@RequestMapping("/diagnostics")
public class DiagnosticController {

    private final PedidoRepository pedidoRepository;

    public DiagnosticController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/check-pedidos")
    @Transactional(readOnly = true)
    public ResponseEntity<String> checkPedidos() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Resultado del Diagnóstico de Pedidos</h2>");
        try {
            List<Pedido> todosLosPedidos = pedidoRepository.findAll();
            if (todosLosPedidos.isEmpty()) {
                result.append("<p style='color: blue;'>[INFO] No hay pedidos en la base de datos para verificar.</p>");
            } else {
                result.append(String.format("<p style='color: green;'>[✓] ¡ÉXITO! Se encontraron %d pedidos. Verificando detalles...</p>", todosLosPedidos.size()));
                result.append("<ul>");
                for (Pedido p : todosLosPedidos) {
                    // Forzamos la carga de los detalles para que el error ocurra aquí si existe
                    int numDetalles = p.getDetalles().size();
                    result.append(String.format("<li>Pedido #%d tiene %d detalles. Total: S/. %.2f</li>", p.getIdPedido(), numDetalles, p.getTotal()));
                }
                result.append("</ul>");
            }
            return ResponseEntity.ok(result.toString());
        } catch (Throwable t) { // Atrapamos 'Throwable' para errores graves como StackOverflowError
            result.append("<div style='border: 2px solid red; padding: 10px; background-color: #ffebeb;'>");
            result.append("<h3 style='color: red;'>[X] ¡ERROR FATAL AL PROCESAR LOS DATOS!</h3>");
            result.append("<p><strong>Tipo de Error:</strong> ").append(t.getClass().getName()).append("</p>");
            result.append("<p><strong>Mensaje:</strong> ").append(t.getMessage()).append("</p>");
            result.append("<hr>");
            result.append("<p><strong>Causa Más Probable:</strong> Un bucle infinito al convertir las entidades a JSON (serialización). Esto suele ocurrir cuando una entidad 'Pedido' se refiere a 'DetallePedido', y 'DetallePedido' se refiere de nuevo a 'Pedido' sin control.</p>");
            result.append("<p><strong>Solución Sugerida:</strong></p>");
            result.append("<ol>");
            result.append("<li>Asegúrate de que en la entidad <strong>Pedido.java</strong>, la lista de detalles tenga la anotación <code>@JsonManagedReference</code>.</li>");
            result.append("<li>Asegúrate de que en la entidad <strong>DetallePedido.java</strong>, la referencia al pedido tenga la anotación <code>@JsonBackReference</code>.</li>");
            result.append("</ol>");
            result.append("</div>");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result.toString());
        }
    }
}