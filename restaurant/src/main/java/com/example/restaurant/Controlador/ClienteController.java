package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.TipoCliente;
import com.example.restaurant.repositorios.ClienteRepository;
import com.example.restaurant.servicio.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {
	
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    /**
     * Muestra la página de gestión de clientes
     */
    @GetMapping
    public String mostrarGestionClientes() {
        return "gestion-clientes";
    }
    
    /**
     * ✅ GUARDAR CLIENTE - CORREGIDO
     */
    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarCliente(@RequestBody Cliente cliente) {
        try {
            System.out.println("🔄 Guardando cliente...");
            System.out.println("📊 Datos recibidos: " + cliente.toString());

            // Validar tipo de cliente
            TipoCliente tipo = cliente.getTipoCliente();
            if (tipo == null) {
                return ResponseEntity.badRequest().body("Tipo de cliente inválido");
            }

            // Validación según el tipo
            if (tipo == TipoCliente.PARTICULAR) {
                String nombreCliente = cliente.getNombreCliente();
                if (nombreCliente == null || nombreCliente.trim().length() < 3 || nombreCliente.trim().length() > 100) {
                    return ResponseEntity.badRequest().body("Nombre debe tener entre 3 y 100 caracteres");
                }
                cliente.setNombreCliente(nombreCliente.trim());
                
            } else if (tipo == TipoCliente.PENSION) {
                String dni = cliente.getDni();
                String nombres = cliente.getNombres();
                String apellidos = cliente.getApellidos();
                
                if (dni == null || !dni.matches("\\d{8}")) {
                    return ResponseEntity.badRequest().body("DNI debe tener exactamente 8 dígitos");
                }
                
                if (nombres == null || nombres.trim().length() < 2) {
                    return ResponseEntity.badRequest().body("Nombres son obligatorios (mínimo 2 caracteres)");
                }
                
                if (apellidos == null || apellidos.trim().length() < 2) {
                    return ResponseEntity.badRequest().body("Apellidos son obligatorios (mínimo 2 caracteres)");
                }
                
                // Verificar si ya existe el DNI
                if (clienteRepository.existsByDni(dni)) {
                    return ResponseEntity.badRequest().body("Ya existe un cliente con el DNI: " + dni);
                }
                
                cliente.setDni(dni.trim());
                cliente.setNombres(nombres.trim());
                cliente.setApellidos(apellidos.trim());
            }

            Cliente clienteGuardado = clienteRepository.save(cliente);
            
            System.out.println("✅ Cliente guardado exitosamente con ID: " + clienteGuardado.getIdCliente());
            
            return ResponseEntity.ok("Cliente guardado exitosamente");

        } catch (Exception e) {
            System.err.println("❌ Error al guardar cliente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    /**
     * ✅ OBTENER TODOS LOS CLIENTES - CORREGIDO
     */
    @GetMapping("/api/buscar/todos")
    @ResponseBody
    public ResponseEntity<Page<Cliente>> obtenerTodosClientes(
            @RequestParam(value = "termino", required = false, defaultValue = "") String termino, 
            Pageable pageable) {

        Page<Cliente> clientes;
        if (termino != null && !termino.isEmpty()) {
            clientes = clienteRepository.findByTermino(termino, pageable);
        } else {
            clientes = clienteRepository.findAll(pageable);
        }
        return ResponseEntity.ok(clientes);
    }
    
    /**
     * ✅ OBTENER CLIENTES PENSIONADOS - CORREGIDO
     */
    @GetMapping("/api/tipo/{tipo}")
    @ResponseBody
    public ResponseEntity<?> buscarPorTipo(@PathVariable("tipo") String tipo) {
        try {
            System.out.println("🔄 Buscando clientes de tipo: " + tipo);
            
            TipoCliente tipoCliente = TipoCliente.valueOf(tipo.toUpperCase());
            List<Cliente> clientes = clienteRepository.findByTipoCliente(tipoCliente);
            
            System.out.println("📊 Clientes de tipo " + tipo + " encontrados: " + clientes.size());
            
            return ResponseEntity.ok(clientes);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tipo de cliente inválido: " + tipo);
            
        } catch (Exception e) {
            System.err.println("❌ Error al buscar clientes por tipo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar clientes: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza un cliente existente
     */
    @PostMapping("/actualizar")
    @ResponseBody
    public ResponseEntity<?> actualizarCliente(@RequestBody Cliente clienteActualizado) {
        try {
            System.out.println("🔄 Actualizando cliente ID: " + clienteActualizado.getIdCliente());
            clienteService.actualizarCliente(clienteActualizado);
            return ResponseEntity.ok("Cliente actualizado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar cliente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al actualizar el cliente.");
        }
    }

    // AÑADE este método para obtener los datos del cliente antes de editar
    @GetMapping("/api/buscar/{id}")
    @ResponseBody
    public ResponseEntity<?> buscarClienteParaEditar(@PathVariable("id") Integer id) {
        Optional<Cliente> cliente = clienteService.buscarClientePorId(id);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * ✅ ELIMINAR CLIENTE - CORREGIDO
     */
    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarCliente(@PathVariable("id") Integer id) {
        try {
            System.out.println("🔄 Eliminando cliente con ID: " + id);
            
            // Verificar si el cliente existe
            Optional<Cliente> clienteOpt = clienteRepository.findById(id);
            if (!clienteOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Cliente cliente = clienteOpt.get();
            
            // Verificar si tiene pedidos asociados (sin cargar la colección completa)
            boolean tienePedidos = cliente.getPedidos() != null && !cliente.getPedidos().isEmpty();
            if (tienePedidos) {
                return ResponseEntity.badRequest()
                    .body("No se puede eliminar el cliente porque tiene pedidos asociados");
            }
            
            // Verificar si tiene pensiones asociadas (sin cargar la colección completa)
            boolean tienePensiones = cliente.getPensiones() != null && !cliente.getPensiones().isEmpty();
            if (tienePensiones) {
                return ResponseEntity.badRequest()
                    .body("No se puede eliminar el cliente porque tiene pensiones asociadas");
            }
            
            clienteRepository.delete(cliente);
            
            System.out.println("✅ Cliente eliminado exitosamente");
            
            return ResponseEntity.ok("Cliente eliminado exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar cliente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el cliente: " + e.getMessage());
        }
    }
    
   
    
    /**
     * Obtiene un cliente para editar
     */
    @GetMapping("/api/editar/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerClienteParaEditar(@PathVariable("id") Integer id) {
        try {
            Optional<Cliente> cliente = clienteService.buscarClientePorId(id);
            
            if (cliente.isPresent()) {
                return ResponseEntity.ok(cliente.get());
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener el cliente");
        }
    }
    
    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Cliente>> buscarPorNombre(@RequestParam(value = "nombre", required = false) String nombre) {
        try {
            List<Cliente> clientes;

            if (nombre == null || nombre.trim().isEmpty()) {
                clientes = clienteService.listarClientes();
            } else {
                clientes = clienteService.buscarClientesPorNombre(nombre);
            }

            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/cajero/busqueda-rapida")
    @ResponseBody
    public ResponseEntity<List<Cliente>> busquedaRapida(@RequestParam(value = "termino") String termino) {
        try {
            if (termino == null || termino.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Cliente> clientes = clienteService.buscarClientesPorNombre(termino.trim());
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
}