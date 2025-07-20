package com.example.restaurant.Controlador;

import com.example.restaurant.dto.DetalleHistorialDTO;
import com.example.restaurant.dto.PedidoHistorialDTO;
import com.example.restaurant.dto.PedidoRequest;
import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.generador.PedidoPdfGenerator;
import com.example.restaurant.repositorios.PedidoRepository;
import com.example.restaurant.servicio.PedidoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Inyectamos el repositorio para usarlo en el historial
    @Autowired
    private PedidoRepository pedidoRepository;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoRequest pedidoRequest) {
        try {
            Pedido nuevoPedido = pedidoService.crearPedidoCompleto(pedidoRequest);
            Map<String, Object> response = Map.of(
                    "message", "Pedido creado exitosamente",
                    "pedidoId", nuevoPedido.getIdPedido()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al crear el pedido: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para generar y descargar un resumen de pedido en formato PDF.
     *
     * @param id El ID del pedido a descargar.
     * @param response La respuesta HTTP para escribir el archivo.
     * @throws IOException Si ocurre un error al escribir el PDF.
     */
    @GetMapping("/pdf/{id}")
    public void descargarPdfPedido(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        try {
            // Usamos el servicio para obtener el pedido completo
            Pedido pedido = pedidoService.obtenerPedidoPorId(id);

            // Configuramos la respuesta HTTP para que el navegador descargue el archivo
            response.setContentType("application/pdf");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=pedido_" + id + ".pdf";
            response.setHeader(headerKey, headerValue);

            // Usamos nuestro generador para crear el PDF y escribirlo en la respuesta
            PedidoPdfGenerator.generarPdf(pedido, response.getOutputStream());

        } catch (IllegalArgumentException e) {
            // Si el pedido no se encuentra, devolvemos un error 404
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Endpoint para obtener la lista de todos los pedidos para la vista de historial.
     *
     * @return Una lista de DTOs con la información formateada.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<PedidoHistorialDTO>> obtenerHistorial() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByIdPedidoDesc();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<PedidoHistorialDTO> historial = pedidos.stream().map(pedido -> {
            PedidoHistorialDTO dto = new PedidoHistorialDTO();
            dto.setIdPedido(pedido.getIdPedido());
            dto.setFecha(pedido.getFecha().format(formatter));
            dto.setNombreCliente(pedido.getCliente().getNombreCliente());
            dto.setNombreUsuario(pedido.getUsuario().getNombreUsuario());
            dto.setTotal(pedido.getTotal());

            List<DetalleHistorialDTO> detallesDto = pedido.getDetalles().stream().map(detalle -> {
                DetalleHistorialDTO detDto = new DetalleHistorialDTO();
                String nombreItem = (detalle.getComida() != null)
                        ? detalle.getComida().getNombreComida()
                        : detalle.getBebida().getNombreBebida();
                detDto.setNombreItem(nombreItem);
                detDto.setCantidad(detalle.getCantidad());
                detDto.setPrecioUnitario(detalle.getPrecioUnitario());
                detDto.setSubtotal(detalle.getSubtotal());
                return detDto;
            }).collect(Collectors.toList());

            dto.setDetalles(detallesDto);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }
}