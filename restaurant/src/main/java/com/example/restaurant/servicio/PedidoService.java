package com.example.restaurant.servicio;

import com.example.restaurant.dto.PedidoRequest;
import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComidaRepository comidaRepository;
    private final BebidaRepository bebidaRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         UsuarioRepository usuarioRepository,
                         ComidaRepository comidaRepository,
                         BebidaRepository bebidaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.comidaRepository = comidaRepository;
        this.bebidaRepository = bebidaRepository;
    }

    @Transactional
    public Pedido crearPedidoCompleto(PedidoRequest request) {
        // 1. Validar y obtener las entidades principales
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + request.getClienteId()));

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        // 2. Crear la cabecera del pedido
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setCliente(cliente);
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setFecha(LocalDateTime.now());
        nuevoPedido.setEstado("PENDIENTE");

        // 3. Procesar y crear los detalles del pedido
        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal totalCalculado = BigDecimal.ZERO;

        // Procesar detalles de comida
        if (request.getDetallesComida() != null) {
            for (PedidoRequest.DetalleComidaRequest detReq : request.getDetallesComida()) {
                Comida comida = comidaRepository.findById(detReq.getComidaId())
                        .orElseThrow(() -> new IllegalArgumentException("Comida no encontrada con ID: " + detReq.getComidaId()));

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(nuevoPedido);
                detalle.setTipoItem(TipoItem.COMIDA);
                detalle.setComida(comida);
                detalle.setCantidad(detReq.getCantidad());
                detalle.setPrecioUnitario(comida.getPrecio());
                BigDecimal subtotal = comida.getPrecio().multiply(new BigDecimal(detReq.getCantidad()));
                detalle.setSubtotal(subtotal);

                detalles.add(detalle);
                totalCalculado = totalCalculado.add(subtotal);
            }
        }

        // Procesar detalles de bebida
        if (request.getDetallesBebida() != null) {
            for (PedidoRequest.DetalleBebidaRequest detReq : request.getDetallesBebida()) {
                Bebida bebida = bebidaRepository.findById(detReq.getBebidaId())
                        .orElseThrow(() -> new IllegalArgumentException("Bebida no encontrada con ID: " + detReq.getBebidaId()));

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(nuevoPedido);
                detalle.setTipoItem(TipoItem.BEBIDA);
                detalle.setBebida(bebida);
                detalle.setCantidad(detReq.getCantidad());
                detalle.setPrecioUnitario(bebida.getPrecio());
                BigDecimal subtotal = bebida.getPrecio().multiply(new BigDecimal(detReq.getCantidad()));
                detalle.setSubtotal(subtotal);

                detalles.add(detalle);
                totalCalculado = totalCalculado.add(subtotal);
            }
        }

        // 4. Asignar el total y los detalles al pedido
        nuevoPedido.setTotal(totalCalculado);
        nuevoPedido.setDetalles(detalles);

        // 5. Guardar el pedido. Gracias a CascadeType.ALL, los detalles se guardan automáticamente.
        return pedidoRepository.save(nuevoPedido);
    }

    // ==================== MÉTODO AÑADIDO ====================
    /**
     * Busca un pedido por su ID.
     * @param id El ID del pedido.
     * @return La entidad Pedido completa con sus detalles.
     * @throws IllegalArgumentException si el pedido no se encuentra.
     */
    public Pedido obtenerPedidoPorId(Integer id) {
        // Usamos findById que devuelve un Optional y lanzamos una excepción si está vacío.
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
    }
    // ========================================================
}