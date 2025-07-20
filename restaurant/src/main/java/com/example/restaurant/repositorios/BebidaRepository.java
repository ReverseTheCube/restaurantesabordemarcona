package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Bebida;
import com.example.restaurant.entidades.TipoBebida;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BebidaRepository extends JpaRepository<Bebida, Integer> {
    Bebida findByNombreBebida(String nombreBebida);
    List<Bebida> findByTipoBebida(TipoBebida tipoBebida);
}
