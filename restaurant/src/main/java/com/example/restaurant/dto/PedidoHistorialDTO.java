package com.example.restaurant.dto;

import java.math.BigDecimal;
import java.util.List;

public class PedidoHistorialDTO {
    private Integer idPedido;
    private String fecha; // Fecha ya formateada como String
    private String nombreCliente;
    private String nombreUsuario;
    private BigDecimal total;
    private List<DetalleHistorialDTO> detalles;

    // Getters y Setters
    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<DetalleHistorialDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleHistorialDTO> detalles) { this.detalles = detalles; }
}