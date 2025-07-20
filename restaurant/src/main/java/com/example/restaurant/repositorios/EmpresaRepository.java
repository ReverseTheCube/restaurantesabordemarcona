package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    
    // Buscar por RUC (único)
    Optional<Empresa> findByRuc(String ruc);
    
    // Verificar si existe por RUC
    boolean existsByRuc(String ruc);
    
    // Buscar por razón social (parcial, case insensitive)
    List<Empresa> findByRazonSocialContainingIgnoreCase(String razonSocial);
    
    // Buscar solo empresas activas
    List<Empresa> findByActivoTrue();
    
    // Buscar empresas por estado
    List<Empresa> findByActivo(Boolean activo);
    
    // Buscar empresas con pensiones activas
    @Query("SELECT DISTINCT e FROM Empresa e " +
           "JOIN e.pensiones p " +
           "WHERE p.fechaFin >= CURRENT_DATE " +
           "AND e.activo = true")
    List<Empresa> findEmpresasConPensionesActivas();
    
    // Contar pensiones por empresa
    @Query("SELECT COUNT(p) FROM Pension p WHERE p.empresa.idEmpresa = :empresaId")
    Long contarPensionesPorEmpresa(@Param("empresaId") Integer empresaId);
    
    // Buscar empresa por RUC o razón social
    @Query("SELECT e FROM Empresa e " +
           "WHERE e.ruc LIKE %:termino% " +
           "OR UPPER(e.razonSocial) LIKE UPPER(CONCAT('%', :termino, '%'))")
    List<Empresa> buscarPorRucOrRazonSocial(@Param("termino") String termino);
}