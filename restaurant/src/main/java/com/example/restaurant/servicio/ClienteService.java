package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.TipoCliente;
import com.example.restaurant.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    /**
     * Registra un nuevo cliente
     */
    public Cliente registrarCliente(String nombre, TipoCliente tipo) {
        Cliente cliente = new Cliente();
        cliente.setNombreCliente(nombre);
        cliente.setTipoCliente(tipo);
        return clienteRepository.save(cliente);
    }
    
    /**
     * Busca un cliente por ID
     */
    public Optional<Cliente> buscarClientePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que 0");
        }
        
        return clienteRepository.findById(id);
    }
    
    /**
     * Lista todos los clientes
     */
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }
    
    /**
     * Actualiza un cliente existente
     */
    public Cliente actualizarCliente(Cliente clienteActualizado) {
        if (clienteActualizado.getIdCliente() == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo para actualizar");
        }

        // Buscar el cliente existente en la base de datos
        Cliente clienteExistente = clienteRepository.findById(clienteActualizado.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteActualizado.getIdCliente()));

        // Validar duplicidad de DNI si ha cambiado
        if (clienteActualizado.getTipoCliente() == TipoCliente.PENSION) {
            if (clienteActualizado.getDni() != null && !clienteActualizado.getDni().equals(clienteExistente.getDni())) {
                if (clienteRepository.existsByDni(clienteActualizado.getDni())) {
                    throw new IllegalArgumentException("El nuevo DNI ya está registrado por otro cliente.");
                }
            }
            clienteExistente.setDni(clienteActualizado.getDni());
            clienteExistente.setNombres(clienteActualizado.getNombres());
            clienteExistente.setApellidos(clienteActualizado.getApellidos());
        } else {
            clienteExistente.setNombreCliente(clienteActualizado.getNombreCliente());
        }

        // El tipo de cliente no se debería poder cambiar en la edición para mantener la integridad

        return clienteRepository.save(clienteExistente);
    }
    
    /**
     * Elimina un cliente
     */
    public void eliminarCliente(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado");
        }
        
        clienteRepository.deleteById(id);
    }
    
    /**
     * Busca clientes por tipo
     */
    public List<Cliente> buscarClientesPorTipo(TipoCliente tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de cliente no puede ser nulo");
        }
        
        return clienteRepository.findByTipoCliente(tipo);
    }
    
    /**
     * Busca clientes por nombre
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) {
        return clienteRepository.findByNombreClienteContainingIgnoreCase(nombre);
    }
    
    /**
     * Obtiene estadísticas básicas - ACTUALIZADO SIN EMPRESA
     */
    public String obtenerEstadisticas() {
        List<Cliente> todos = clienteRepository.findAll();
        
        long pensionados = todos.stream()
                .filter(c -> c.getTipoCliente() == TipoCliente.PENSION)
                .count();
        
        long particulares = todos.stream()
                .filter(c -> c.getTipoCliente() == TipoCliente.PARTICULAR)
                .count();
        
        return String.format("Total: %d | Pensionados: %d | Particulares: %d",
                todos.size(), pensionados, particulares);
    }
    
    /**
     * Guarda un cliente (usado por el controlador)
     */
    public void guardarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }
    
    /**
     * Busca clientes pensionados por nombre o DNI
     */
    public List<Cliente> buscarClientesPensionados(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return clienteRepository.findByTipoCliente(TipoCliente.PENSION);
        }
        
        // Si existe el método en el repository, usarlo
        try {
            return clienteRepository.buscarPensionadosPorNombreODni(termino);
        } catch (Exception e) {
            // Si no existe el método, usar búsqueda básica
            return clienteRepository.findByTipoCliente(TipoCliente.PENSION);
        }
    }
    
    /**
     * Busca por término general (nombre, DNI, etc.)
     */
    public List<Cliente> buscarPorTerminoGeneral(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarClientes();
        }
        
        // Si existe el método en el repository, usarlo
        try {
            return clienteRepository.buscarPorTerminoGeneral(termino);
        } catch (Exception e) {
            // Si no existe el método, usar búsqueda básica por nombre
            return clienteRepository.findByNombreClienteContainingIgnoreCase(termino);
        }
    }
    
    /**
     * Valida los datos de un cliente antes de guardar
     */
    public boolean validarCliente(Cliente cliente) {
        if (cliente.getTipoCliente() == TipoCliente.PENSION) {
            return cliente.getDni() != null && !cliente.getDni().isEmpty() &&
                   cliente.getNombres() != null && !cliente.getNombres().isEmpty() &&
                   cliente.getApellidos() != null && !cliente.getApellidos().isEmpty();
        } else if (cliente.getTipoCliente() == TipoCliente.PARTICULAR) {
            return cliente.getNombreCliente() != null && !cliente.getNombreCliente().isEmpty();
        }
        return false;
    }
}