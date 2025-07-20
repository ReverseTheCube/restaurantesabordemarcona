package com.example.restaurant.dto;

import java.util.List;

/**
 * DTO (Data Transfer Object) que representa la estructura de la petición
 * para crear un nuevo pedido desde el frontend.
 */
public class PedidoRequest {

    private Integer clienteId;
    private Integer usuarioId;
    private List<DetalleComidaRequest> detallesComida;
    private List<DetalleBebidaRequest> detallesBebida;

    // --- Clases anidadas para los detalles ---

    public static class DetalleComidaRequest {
        private Integer comidaId;
        private int cantidad;

        // Getters y Setters
        public Integer getComidaId() {
            return comidaId;
        }
        public void setComidaId(Integer comidaId) {
            this.comidaId = comidaId;
        }
        public int getCantidad() {
            return cantidad;
        }
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    public static class DetalleBebidaRequest {
        private Integer bebidaId;
        private int cantidad;

        // Getters y Setters
        public Integer getBebidaId() {
            return bebidaId;
        }
        public void setBebidaId(Integer bebidaId) {
            this.bebidaId = bebidaId;
        }
        public int getCantidad() {
            return cantidad;
        }
        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    // --- Getters y Setters para la clase principal ---

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<DetalleComidaRequest> getDetallesComida() {
        return detallesComida;
    }

    public void setDetallesComida(List<DetalleComidaRequest> detallesComida) {
        this.detallesComida = detallesComida;
    }

    public List<DetalleBebidaRequest> getDetallesBebida() {
        return detallesBebida;
    }

    public void setDetallesBebida(List<DetalleBebidaRequest> detallesBebida) {
        this.detallesBebida = detallesBebida;
    }
}