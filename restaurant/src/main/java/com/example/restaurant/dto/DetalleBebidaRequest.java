package com.example.restaurant.dto;

public class DetalleBebidaRequest {
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