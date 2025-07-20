package com.example.restaurant.dto;

import java.util.Map;

public class PedidoDTO {
    private Map<String, ItemDTO> platos;
    private Map<String, ItemDTO> bebidas;

    public Map<String, ItemDTO> getPlatos() {
        return platos;
    }

    public void setPlatos(Map<String, ItemDTO> platos) {
        this.platos = platos;
    }

    public Map<String, ItemDTO> getBebidas() {
        return bebidas;
    }

    public void setBebidas(Map<String, ItemDTO> bebidas) {
        this.bebidas = bebidas;
    }
}
