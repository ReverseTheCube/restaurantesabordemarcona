package com.example.restaurant.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) para unificar Comida y Bebida en un solo
 * formato para ser enviado al frontend.
 */
public class ItemDTO {
    private Integer id;
    private String nombre;
    private BigDecimal precio;
    private String tipo;      // "comida" o "bebida"
    private String categoria; // "plato", "entrada", "gaseosa", "jugo"

    // Constructor para Comida
    public ItemDTO(Integer id, String nombre, BigDecimal precio, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = "comida";
        this.categoria = categoria;
    }

    // Constructor para Bebida (con sobrecarga para diferenciar)
    public ItemDTO(Integer id, String nombre, BigDecimal precio, String categoria, boolean esBebida) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = "bebida";
        this.categoria = categoria;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}