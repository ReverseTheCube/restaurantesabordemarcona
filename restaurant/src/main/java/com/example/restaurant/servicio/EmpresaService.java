package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.repositorios.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmpresaService {
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * Registra una nueva empresa
     */
    public Empresa registrarEmpresa(String ruc, String razonSocial) {
        // Validar que no exista el RUC
        if (empresaRepository.existsByRuc(ruc)) {
            throw new IllegalArgumentException("Ya existe una empresa con el RUC: " + ruc);
        }
        
        // Validar datos
        if (ruc == null || !ruc.matches("\\d{11}")) {
            throw new IllegalArgumentException("RUC debe tener exactamente 11 dígitos");
        }
        
        if (razonSocial == null || razonSocial.trim().length() < 3) {
            throw new IllegalArgumentException("Razón social debe tener al menos 3 caracteres");
        }
        
        Empresa empresa = new Empresa(ruc, razonSocial.trim());
        return empresaRepository.save(empresa);
    }
    
    /**
     * Busca una empresa por ID
     */
    public Optional<Empresa> buscarPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que 0");
        }
        return empresaRepository.findById(id);
    }
    
    /**
     * Busca una empresa por RUC
     */
    public Optional<Empresa> buscarPorRuc(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            throw new IllegalArgumentException("RUC no puede estar vacío");
        }
        return empresaRepository.findByRuc(ruc.trim());
    }
    
    /**
     * Lista todas las empresas activas
     */
    public List<Empresa> listarEmpresasActivas() {
        return empresaRepository.findByActivoTrue();
    }
    
    /**
     * Lista todas las empresas
     */
    public List<Empresa> listarTodasLasEmpresas() {
        return empresaRepository.findAll();
    }
    
    /**
     * Busca empresas por razón social
     */
    public List<Empresa> buscarPorRazonSocial(String razonSocial) {
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            return listarEmpresasActivas();
        }
        return empresaRepository.findByRazonSocialContainingIgnoreCase(razonSocial.trim());
    }
    
    /**
     * Busca empresas por RUC o razón social
     */
    public List<Empresa> buscarEmpresas(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarEmpresasActivas();
        }
        return empresaRepository.buscarPorRucOrRazonSocial(termino.trim());
    }
    
    /**
     * Actualiza una empresa
     */
    public Empresa actualizarEmpresa(Integer id, String nuevaRazonSocial) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        
        if (nuevaRazonSocial == null || nuevaRazonSocial.trim().length() < 3) {
            throw new IllegalArgumentException("Razón social debe tener al menos 3 caracteres");
        }
        
        empresa.setRazonSocial(nuevaRazonSocial.trim());
        return empresaRepository.save(empresa);
    }
    
    /**
     * Desactiva una empresa (no la elimina)
     */
    public void desactivarEmpresa(Integer id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        
        empresa.setActivo(false);
        empresaRepository.save(empresa);
    }
    
    /**
     * Activa una empresa
     */
    public void activarEmpresa(Integer id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        
        empresa.setActivo(true);
        empresaRepository.save(empresa);
    }
    
    /**
     * Elimina una empresa (solo si no tiene pensiones)
     */
    public void eliminarEmpresa(Integer id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
        
        Long cantidadPensiones = empresaRepository.contarPensionesPorEmpresa(id);
        if (cantidadPensiones > 0) {
            throw new IllegalStateException("No se puede eliminar la empresa porque tiene " + 
                                          cantidadPensiones + " pensión(es) asociada(s)");
        }
        
        empresaRepository.delete(empresa);
    }
    
    /**
     * Obtiene empresas con pensiones activas
     */
    public List<Empresa> obtenerEmpresasConPensionesActivas() {
        return empresaRepository.findEmpresasConPensionesActivas();
    }
    
    /**
     * Cuenta las pensiones de una empresa
     */
    public Long contarPensiones(Integer empresaId) {
        return empresaRepository.contarPensionesPorEmpresa(empresaId);
    }
    
    /**
     * Verifica si un RUC ya existe
     */
    public boolean existeRuc(String ruc) {
        return empresaRepository.existsByRuc(ruc);
    }
}