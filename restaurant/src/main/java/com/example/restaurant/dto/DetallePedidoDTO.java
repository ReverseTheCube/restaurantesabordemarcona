package com.example.restaurant.dto;

import java.math.BigDecimal;

// Esta clase representa un solo item dentro de un pedido para el historial.
public class DetallePedidoDTO {
    private String nombreItem;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    // Getters y Setters
    public String getNombreItem() { return nombreItem; }
    public void setNombreItem(String nombreItem) { this.nombreItem = nombreItem; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}