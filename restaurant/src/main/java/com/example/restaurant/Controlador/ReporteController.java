package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.generador.HistorialExcelGenerador;
import com.example.restaurant.generador.PedidoExcelGenerador;
import com.example.restaurant.repositorios.PedidoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes") // Nueva ruta base, más clara
public class ReporteController {

    private final PedidoRepository pedidoRepository;

    public ReporteController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Genera un archivo Excel para un pedido específico.
     * Reemplaza la lógica del antiguo AsciiExcelController.
     *
     * @param pedidoId El ID del pedido a exportar.
     * @param response La respuesta HTTP para escribir el archivo.
     */
    @GetMapping("/pedido/{pedidoId}/excel")
    public void generarExcelPedidoUnico(@PathVariable Integer pedidoId, HttpServletResponse response) throws IOException {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = String.format("pedido_%d_%s.xlsx", pedido.getIdPedido(), LocalDate.now());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        PedidoExcelGenerador.generarExcel(pedido, response.getOutputStream());
    }

    /**
     * Genera un archivo Excel con el historial completo de todos los pedidos.
     * Este es el endpoint que usará el botón "Descargar Reporte" en historial.html.
     *
     * @param response La respuesta HTTP para escribir el archivo.
     */
    @GetMapping("/historial/excel")
    public void generarExcelHistorialCompleto(HttpServletResponse response) throws IOException {
        // Obtenemos todos los pedidos, ordenados por ID descendente (los más nuevos primero)
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByIdPedidoDesc();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = String.format("historial_ventas_%s.xlsx", LocalDate.now());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        HistorialExcelGenerador.generarExcel(pedidos, response.getOutputStream());
    }
}