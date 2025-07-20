package com.example.restaurant.dto;

public class ItemRequest {
    // MEJORA: Cambiamos 'id' por 'nombre' para que coincida con lo que envía el frontend
    private String nombre;
    private String tipo;
    private int cantidad;

    // --- Getters y Setters para nombre, tipo y cantidad ---
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}