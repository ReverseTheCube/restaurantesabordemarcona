package com.example.restaurant.servicio;

import com.example.restaurant.dto.DetalleCompraDTO;
import com.example.restaurant.entidades.Compra;
import com.example.restaurant.entidades.DetalleCompra;
import com.example.restaurant.entidades.Insumo;
import com.example.restaurant.repositorios.CompraRepository;
import com.example.restaurant.repositorios.DetalleCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final InventarioService inventarioService;

    @Autowired
    public CompraService(CompraRepository compraRepository,
                         DetalleCompraRepository detalleCompraRepository,
                         InventarioService inventarioService) {
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.inventarioService = inventarioService;
    }

    @Transactional
    public void registrarCompra(List<DetalleCompraDTO> detalles) {
        // Crear entidad Compra
        Compra compra = new Compra();
        compra.setFechaCompra(LocalDate.now());
        Compra compraGuardada = compraRepository.save(compra);

        // Procesar detalles
        List<DetalleCompra> detallesEntities = new ArrayList<>();
        
        for (DetalleCompraDTO dto : detalles) {
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compraGuardada);
            
            Insumo insumo = new Insumo();
            insumo.setIdInsumo(dto.getIdInsumo());
            detalle.setInsumo(insumo);
            
            detalle.setCantidad(dto.getCantidad());
            detalle.setPrecioCompra(dto.getPrecioCompra());
            detalle.setSubtotal(dto.getCantidad() * dto.getPrecioCompra());
            
            detallesEntities.add(detalle);
            
            // Actualizar stock
            inventarioService.actualizarStock(dto.getIdInsumo(), dto.getCantidad());
        }
        
        // Guardar detalles
        detalleCompraRepository.saveAll(detallesEntities);
    }
}