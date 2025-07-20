package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
    List<Insumo> findByNombreInsumoContainingIgnoreCase(String nombre);
    Optional<Insumo> findByIdInsumo(Integer id);
}