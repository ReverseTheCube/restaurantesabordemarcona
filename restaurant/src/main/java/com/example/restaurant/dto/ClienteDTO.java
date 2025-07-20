package com.example.restaurant.dto;

import com.example.restaurant.entidades.TipoCliente;

/**
 * DTO simple para transferencia de datos de Cliente
 */
public class ClienteDTO {
    
    private Integer id;
    private String nombre;
    private TipoCliente tipoCliente;
    private Integer cantidadPedidos;
    private Integer cantidadPensiones;
    
    // Constructores
    public ClienteDTO() {
    }
    
    public ClienteDTO(Integer id, String nombre, TipoCliente tipoCliente) {
        this.id = id;
        this.nombre = nombre;
        this.tipoCliente = tipoCliente;
    }
    
    // Getters y Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }
    
    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
    
    public Integer getCantidadPedidos() {
        return cantidadPedidos;
    }
    
    public void setCantidadPedidos(Integer cantidadPedidos) {
        this.cantidadPedidos = cantidadPedidos;
    }
    
    public Integer getCantidadPensiones() {
        return cantidadPensiones;
    }
    
    public void setCantidadPensiones(Integer cantidadPensiones) {
        this.cantidadPensiones = cantidadPensiones;
    }
}