package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Pension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PensionRepository extends JpaRepository<Pension, Integer> {
    
    // ✅ NUEVO: Método para obtener todas las pensiones con datos cargados (EAGER)
    @Query("SELECT p FROM Pension p " +
           "LEFT JOIN FETCH p.cliente c " +
           "LEFT JOIN FETCH p.empresa e " +
           "ORDER BY p.idPension DESC")
    List<Pension> findAllWithDetails();
    
    // Buscar pensiones por empresa
    List<Pension> findByEmpresa_IdEmpresa(Integer empresaId);
    
    // Buscar pensiones por cliente
    List<Pension> findByCliente_IdCliente(Integer clienteId);
    
    // Buscar pensiones activas (que están vigentes hoy)
    @Query("SELECT p FROM Pension p " +
           "WHERE p.fechaInicio <= :fecha " +
           "AND p.fechaFin >= :fecha")
    List<Pension> findPensionesActivas(@Param("fecha") LocalDate fecha);
    
    // Buscar pensiones que vencen pronto
    @Query("SELECT p FROM Pension p " +
           "WHERE p.fechaFin BETWEEN :fechaInicio AND :fechaFin")
    List<Pension> findPensionesQueVencenEntre(@Param("fechaInicio") LocalDate fechaInicio, 
                                              @Param("fechaFin") LocalDate fechaFin);
    
    // Contar pensiones activas por empresa
    @Query("SELECT COUNT(p) FROM Pension p " +
           "WHERE p.empresa.idEmpresa = :empresaId " +
           "AND p.fechaInicio <= CURRENT_DATE " +
           "AND p.fechaFin >= CURRENT_DATE")
    Long contarPensionesActivasPorEmpresa(@Param("empresaId") Integer empresaId);
    
    // Obtener el monto total como BigDecimal
    @Query("SELECT SUM(p.montoMensual) FROM Pension p " +
           "WHERE p.cliente.idCliente = :clienteId " +
           "AND p.fechaInicio <= CURRENT_DATE " +
           "AND p.fechaFin >= CURRENT_DATE")
    BigDecimal obtenerMontoTotalPorCliente(@Param("clienteId") Integer clienteId);
    
    // Buscar pensiones por rango de fechas
    @Query("SELECT p FROM Pension p " +
           "WHERE (p.fechaInicio BETWEEN :fechaInicio AND :fechaFin) " +
           "OR (p.fechaFin BETWEEN :fechaInicio AND :fechaFin) " +
           "OR (p.fechaInicio <= :fechaInicio AND p.fechaFin >= :fechaFin)")
    List<Pension> findPensionesPorRangoFechas(@Param("fechaInicio") LocalDate fechaInicio, 
                                              @Param("fechaFin") LocalDate fechaFin);
    
    // Buscar pensiones por empresa y estado activo
    @Query("SELECT p FROM Pension p " +
           "WHERE p.empresa.idEmpresa = :empresaId " +
           "AND p.empresa.activo = true " +
           "ORDER BY p.fechaInicio DESC")
    List<Pension> findByEmpresaActivaOrderByFecha(@Param("empresaId") Integer empresaId);
}