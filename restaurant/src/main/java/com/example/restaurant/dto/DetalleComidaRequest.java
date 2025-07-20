package com.example.restaurant.dto;

public class DetalleComidaRequest {
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