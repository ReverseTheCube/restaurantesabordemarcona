package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Insumo;
import com.example.restaurant.repositorios.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private final InsumoRepository insumoRepository;

    @Autowired
    public InventarioService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    public List<Insumo> buscarInsumosPorNombre(String nombre) {
        return insumoRepository.findByNombreInsumoContainingIgnoreCase(nombre);
    }

    public Optional<Insumo> obtenerInsumoPorId(Integer id) {
        return insumoRepository.findById(id);
    }

    @Transactional
    public void actualizarStock(Integer insumoId, Integer cantidad) {
        insumoRepository.findById(insumoId).ifPresent(insumo -> {
            int nuevoStock = insumo.getStock() + cantidad;
            insumo.setStock(nuevoStock);
            insumoRepository.save(insumo);
        });
    }
}