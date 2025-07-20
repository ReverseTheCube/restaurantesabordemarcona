package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Comida;
import com.example.restaurant.repositorios.ComidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComidaService {
    @Autowired
    private ComidaRepository comidaRepository;

    public List<Comida> listarComidas() {
        return comidaRepository.findAll();
    }

    public Comida guardarComida(Comida comida) {
        return comidaRepository.save(comida);
    }

    // CORRECCIÓN: El ID debe ser Integer, igual que en el repositorio.
    public Comida buscarPorId(Integer id) {
        return comidaRepository.findById(id).orElse(null);
    }

    // CORRECCIÓN: El ID debe ser Integer, igual que en el repositorio.
    public void eliminarComida(Integer id) {
        comidaRepository.deleteById(id);
    }
}