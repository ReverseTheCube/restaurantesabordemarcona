package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.servicio.EmpresaService;
import com.example.restaurant.repositorios.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {
    
    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * ✅ GUARDAR EMPRESA - CORREGIDO
     */
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarEmpresa(@RequestBody Empresa empresa) {
        try {
            System.out.println("🔄 Guardando empresa...");
            System.out.println("📊 Datos recibidos: " + empresa.toString());
            
            String ruc = empresa.getRuc();
            String razonSocial = empresa.getRazonSocial();
            
            if (ruc == null || !ruc.matches("\\d{11}")) {
                return ResponseEntity.badRequest().body("RUC debe tener exactamente 11 dígitos");
            }
            
            if (razonSocial == null || razonSocial.trim().length() < 3) {
                return ResponseEntity.badRequest().body("Razón social debe tener al menos 3 caracteres");
            }
            
            // Verificar si ya existe el RUC
            if (empresaRepository.existsByRuc(ruc)) {
                return ResponseEntity.badRequest().body("Ya existe una empresa con el RUC: " + ruc);
            }
            
            // Limpiar datos
            empresa.setRuc(ruc.trim());
            empresa.setRazonSocial(razonSocial.trim());
            
            Empresa empresaGuardada = empresaRepository.save(empresa);
            
            System.out.println("✅ Empresa guardada exitosamente con ID: " + empresaGuardada.getIdEmpresa());
            
            return ResponseEntity.ok("Empresa registrada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al guardar empresa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
    
    /**
     * ✅ LISTAR EMPRESAS ACTIVAS - CORREGIDO
     */
    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<Page<Empresa>> listarEmpresas(
            @RequestParam(value = "termino", required = false, defaultValue = "") String termino, 
            Pageable pageable) {

        Page<Empresa> empresas;
        if (termino != null && !termino.isEmpty()) {
            empresas = empresaRepository.findByTermino(termino, pageable);
        } else {
            empresas = empresaRepository.findByActivoTrue(pageable);
        }
        return ResponseEntity.ok(empresas);
    }
    
    
    /**
     * Buscar empresas por término (RUC o razón social)
     */
    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Empresa>> buscarEmpresas(@RequestParam(required = false) String termino) {
        try {
            List<Empresa> empresas = empresaService.buscarEmpresas(termino);
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Actualizar empresa
     */
    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarEmpresa(@RequestBody Empresa empresaActualizada) {
        try {
            System.out.println("🔄 Actualizando empresa ID: " + empresaActualizada.getIdEmpresa());
            empresaService.actualizarEmpresa(empresaActualizada.getIdEmpresa(), empresaActualizada.getRazonSocial());
            return ResponseEntity.ok("Empresa actualizada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar empresa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al actualizar la empresa.");
        }
    }

    // AÑADE este método para obtener los datos de la empresa
    @GetMapping("/api/buscar/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarEmpresaParaEditar(@PathVariable("id") Integer id) {
        Optional<Empresa> empresa = empresaService.buscarPorId(id);
        if (empresa.isPresent()) {
            return ResponseEntity.ok(empresa.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * ✅ ELIMINAR EMPRESA - CORREGIDO
     */
    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarEmpresa(@PathVariable("id") Integer id) {
        try {
            System.out.println("🔄 Eliminando empresa con ID: " + id);
            
            Optional<Empresa> empresaOpt = empresaRepository.findById(id);
            if (!empresaOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Empresa empresa = empresaOpt.get();
            
            // Verificar si tiene pensiones asociadas (sin cargar la colección completa)
            Long cantidadPensiones = empresaRepository.contarPensionesPorEmpresa(id);
            if (cantidadPensiones > 0) {
                return ResponseEntity.badRequest()
                    .body("No se puede eliminar la empresa porque tiene " + cantidadPensiones + " pensión(es) asociada(s)");
            }
            
            empresaRepository.delete(empresa);
            
            System.out.println("✅ Empresa eliminada exitosamente");
            
            return ResponseEntity.ok("Empresa eliminada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar empresa: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la empresa: " + e.getMessage());
        }
    }
    
    /**
     * Obtener empresas con pensiones activas
     */
    @GetMapping("/api/con-pensiones")
    @ResponseBody
    public ResponseEntity<List<Empresa>> obtenerEmpresasConPensiones() {
        try {
            List<Empresa> empresas = empresaService.obtenerEmpresasConPensionesActivas();
            return ResponseEntity.ok(empresas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}