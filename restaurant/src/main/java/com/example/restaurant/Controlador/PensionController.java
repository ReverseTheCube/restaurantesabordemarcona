package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.Empresa;
import com.example.restaurant.entidades.Pension;
import com.example.restaurant.servicio.ClienteService;
import com.example.restaurant.servicio.EmpresaService;
import com.example.restaurant.repositorios.PensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.restaurant.dto.PensionDTO;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pensiones")
@CrossOrigin(origins = "*")
public class PensionController {
    
    @Autowired
    private PensionRepository pensionRepository;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private EmpresaService empresaService;
    
    /**
     * ✅ LISTAR PENSIONES - VERSIÓN SIMPLIFICADA Y ROBUSTA
     */
    @GetMapping("/api/listar")
    @ResponseBody
    @Transactional(readOnly = true) // <-- Importante para acceder a datos lazy
    public ResponseEntity<Page<PensionDTO>> listarPensiones(Pageable pageable) {
        try {
            Page<Pension> pensionesEntidad = pensionRepository.findAll(pageable);

            // Aquí está la magia: convertimos cada Pension a PensionDTO
            Page<PensionDTO> pensionesDTO = pensionesEntidad.map(pension -> new PensionDTO(pension));

            return ResponseEntity.ok(pensionesDTO);

        } catch (Exception e) {
            System.err.println("❌ [PENSION] Error al cargar pensiones paginadas: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * ✅ REGISTRAR PENSIÓN - CON LOGS MEJORADOS
     */
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarPension(@RequestBody Map<String, Object> pensionData) {
        try {
            System.out.println("🔄 [PENSION] Registrando nueva pensión...");
            System.out.println("📊 [PENSION] Datos recibidos: " + pensionData);
            
            // Extraer y validar datos
            Integer empresaId = (Integer) pensionData.get("empresaId");
            Integer clienteId = (Integer) pensionData.get("clienteId");
            
            BigDecimal montoMensual;
            Object montoObj = pensionData.get("montoMensual");
            if (montoObj instanceof Number) {
                montoMensual = BigDecimal.valueOf(((Number) montoObj).doubleValue());
            } else {
                montoMensual = new BigDecimal(montoObj.toString());
            }
            
            String fechaInicioStr = (String) pensionData.get("fechaInicio");
            String fechaFinStr = (String) pensionData.get("fechaFin");
            
            // Validaciones
            if (empresaId == null || clienteId == null || montoMensual == null || 
                fechaInicioStr == null || fechaFinStr == null) {
                return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
            }
            
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr);
            
            if (fechaFin.isBefore(fechaInicio) || fechaFin.isEqual(fechaInicio)) {
                return ResponseEntity.badRequest().body("La fecha de fin debe ser posterior a la fecha de inicio");
            }
            
            if (montoMensual.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El monto mensual debe ser mayor a 0");
            }
            
            // Buscar entidades
            Optional<Empresa> empresaOpt = empresaService.buscarPorId(empresaId);
            Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(clienteId);
            
            if (!empresaOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Empresa no encontrada con ID: " + empresaId);
            }
            
            if (!clienteOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Cliente no encontrado con ID: " + clienteId);
            }
            
            Cliente cliente = clienteOpt.get();
            if (!cliente.getTipoCliente().equals(com.example.restaurant.entidades.TipoCliente.PENSION)) {
                return ResponseEntity.badRequest().body("El cliente debe ser de tipo PENSIONADO");
            }
            
            // Crear y guardar pensión
            Pension pension = new Pension();
            pension.setEmpresa(empresaOpt.get());
            pension.setCliente(cliente);
            pension.setMontoMensual(montoMensual);
            pension.setFechaInicio(fechaInicio);
            pension.setFechaFin(fechaFin);
            
            Pension pensionGuardada = pensionRepository.save(pension);
            
            System.out.println("✅ [PENSION] Pensión registrada exitosamente con ID: " + pensionGuardada.getIdPension());
            
            return ResponseEntity.ok("Pensión registrada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ [PENSION] Error al registrar pensión: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
    
    /**
     * ✅ ELIMINAR PENSIÓN - SIMPLIFICADO
     */
    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarPension(@PathVariable("id") Integer id) {
        try {
            System.out.println("🔄 [PENSION] Eliminando pensión con ID: " + id);
            
            if (!pensionRepository.existsById(id)) {
                System.out.println("❌ [PENSION] Pensión no encontrada con ID: " + id);
                return ResponseEntity.notFound().build();
            }
            
            pensionRepository.deleteById(id);
            
            System.out.println("✅ [PENSION] Pensión eliminada exitosamente");
            
            return ResponseEntity.ok("Pensión eliminada exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ [PENSION] Error al eliminar pensión: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la pensión: " + e.getMessage());
        }
    }
    
    // Los demás métodos se mantienen igual...
    @GetMapping("/api/listar/activas")
    @ResponseBody
    public ResponseEntity<List<Pension>> listarPensionesActivas() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Pension> pensiones = pensionRepository.findPensionesActivas(hoy);
            return ResponseEntity.ok(pensiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/api/empresa/{empresaId}")
    @ResponseBody
    public ResponseEntity<List<Pension>> listarPensionesPorEmpresa(@PathVariable Integer empresaId) {
        try {
            List<Pension> pensiones = pensionRepository.findByEmpresa_IdEmpresa(empresaId);
            return ResponseEntity.ok(pensiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/api/cliente/{clienteId}")
    @ResponseBody
    public ResponseEntity<List<Pension> > listarPensionesPorCliente(@PathVariable Integer clienteId) {
        try {
            List<Pension> pensiones = pensionRepository.findByCliente_IdCliente(clienteId);
            return ResponseEntity.ok(pensiones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarPension(@RequestBody Map<String, Object> payload) {
        try {
            // Extraer los datos del payload que envía el JavaScript
            Integer idPension = (Integer) payload.get("idPension");
            BigDecimal montoMensual = new BigDecimal(payload.get("montoMensual").toString());
            LocalDate fechaInicio = LocalDate.parse(payload.get("fechaInicio").toString());
            LocalDate fechaFin = LocalDate.parse(payload.get("fechaFin").toString());

            System.out.println("🔄 Actualizando pensión ID: " + idPension);

            // Buscar la pensión existente en la base de datos
            Pension existente = pensionRepository.findById(idPension)
                .orElseThrow(() -> new IllegalArgumentException("Pensión no encontrada con ID: " + idPension));

            // Validaciones de negocio
            if (fechaFin.isBefore(fechaInicio)) {
                return ResponseEntity.badRequest().body("La fecha de fin no puede ser anterior a la fecha de inicio.");
            }
            if (montoMensual.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("El monto mensual debe ser un valor positivo.");
            }

            // Actualizar solo los campos permitidos
            existente.setMontoMensual(montoMensual);
            existente.setFechaInicio(fechaInicio);
            existente.setFechaFin(fechaFin);
            
            // Guardar los cambios en la base de datos
            pensionRepository.save(existente);
            
            System.out.println("✅ Pensión actualizada exitosamente.");
            return ResponseEntity.ok("Pensión actualizada exitosamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar pensión: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al actualizar la pensión.");
        }
    }

    // AÑADE este método para obtener los datos de la pensión
    @GetMapping("/api/buscar/{id}")
    @ResponseBody
    @Transactional(readOnly = true) // <-- Añade esta anotación para mantener la sesión abierta
    public ResponseEntity<?> buscarPensionParaEditar(@PathVariable("id") Integer id) {
        Optional<Pension> pensionOpt = pensionRepository.findById(id);

        if (pensionOpt.isPresent()) {
            // Convierte la entidad a DTO antes de devolverla
            PensionDTO dto = new PensionDTO(pensionOpt.get());
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }
    @GetMapping("/api/por-empresa/{empresaId}")
    @ResponseBody
    public ResponseEntity<List<PensionDTO>> getPensionesPorEmpresa(@PathVariable("empresaId") Integer empresaId) {
        List<PensionDTO> pensiones = pensionRepository.findPensionsByEmpresaId(empresaId);
        return ResponseEntity.ok(pensiones);
    }

    @GetMapping("/api/por-cliente/{clienteId}")
    @ResponseBody
    public ResponseEntity<List<PensionDTO>> getPensionesPorCliente(@PathVariable("clienteId") Integer clienteId) {
        List<PensionDTO> pensiones = pensionRepository.findPensionsByClienteId(clienteId);
        return ResponseEntity.ok(pensiones);
    }
}