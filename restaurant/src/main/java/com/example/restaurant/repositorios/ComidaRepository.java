package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Comida;
import com.example.restaurant.entidades.TipoComida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ComidaRepository extends JpaRepository<Comida, Integer> {
    Comida findByNombreComida(String nombreComida);
    List<Comida> findByTipoComida(TipoComida tipoComida);
}
