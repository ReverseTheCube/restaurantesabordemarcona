package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    // ✅ MÉTODOS BÁSICOS:
    List<Cliente> findByNombreClienteContainingIgnoreCase(String nombre);
    List<Cliente> findByTipoCliente(TipoCliente tipoCliente);
    
    // ✅ MÉTODOS PARA PENSIONADOS (NECESARIOS):
    
    // Buscar por DNI (para pensionados)
    Optional<Cliente> findByDni(String dni);
    
    // ✅ CRÍTICO: Verificar si existe un DNI (usado en ClienteController)
    boolean existsByDni(String dni);
    
    // Buscar por nombres o apellidos (para pensionados)
    List<Cliente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
    
    // ✅ BÚSQUEDAS AVANZADAS:
    
    // Buscar clientes pensionados por nombre completo
    @Query("SELECT c FROM Cliente c " +
           "WHERE c.tipoCliente = 'PENSION' " +
           "AND (UPPER(CONCAT(c.nombres, ' ', c.apellidos)) LIKE UPPER(CONCAT('%', :termino, '%')) " +
           "OR c.dni LIKE %:termino%)")
    List<Cliente> buscarPensionadosPorNombreODni(@Param("termino") String termino);
    
    // Buscar todos los clientes por término general (nombre, DNI, etc.)
    @Query("SELECT c FROM Cliente c " +
           "WHERE UPPER(c.nombreCliente) LIKE UPPER(CONCAT('%', :termino, '%')) " +
           "OR UPPER(CONCAT(COALESCE(c.nombres, ''), ' ', COALESCE(c.apellidos, ''))) LIKE UPPER(CONCAT('%', :termino, '%')) " +
           "OR c.dni LIKE %:termino%")
    List<Cliente> buscarPorTerminoGeneral(@Param("termino") String termino);
    
    // Obtener solo clientes pensionados (para el select de pensiones)
    @Query("SELECT c FROM Cliente c WHERE c.tipoCliente = 'PENSION' ORDER BY c.nombres, c.apellidos")
    List<Cliente> findClientesPensionadosOrdenados();
}