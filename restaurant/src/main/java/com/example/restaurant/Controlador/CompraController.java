// V2
package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/compras")
public class CompraController {

    private final InsumoRepository insumoRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;

    @Autowired
    public CompraController(InsumoRepository insumoRepository,
                            CompraRepository compraRepository,
                            DetalleCompraRepository detalleCompraRepository) {
        this.insumoRepository = insumoRepository;
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
    }

    @GetMapping
    public String mostrarFormularioCompras(Model model) {
        model.addAttribute("insumos", insumoRepository.findAll());
        model.addAttribute("detalles", new ArrayList<DetalleCompra>());
        return "compras";
    }

    @PostMapping("/registrar")
    public String registrarCompra(@RequestParam("insumoIds") List<Integer> insumoIds,
                                 @RequestParam("cantidades") List<Integer> cantidades,
                                 @RequestParam("precios") List<Double> precios,
                                 RedirectAttributes redirectAttributes) {
        // Crear la compra
        Compra compra = new Compra();
        compra.setFechaCompra(LocalDate.now());
        Compra compraGuardada = compraRepository.save(compra);

        // Registrar detalles de compra
        for (int i = 0; i < insumoIds.size(); i++) {
            Insumo insumo = insumoRepository.findById(insumoIds.get(i)).orElse(null);
            if (insumo != null) {
                DetalleCompra detalle = new DetalleCompra();
                detalle.setCompra(compraGuardada);
                detalle.setInsumo(insumo);
                detalle.setCantidad(cantidades.get(i));
                detalle.setPrecioCompra(precios.get(i));
                detalle.setSubtotal(cantidades.get(i) * precios.get(i));
                detalleCompraRepository.save(detalle);

                // Actualizar stock
                insumo.setStock(insumo.getStock() + cantidades.get(i));
                insumoRepository.save(insumo);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Compra registrada exitosamente");
        return "redirect:/compras";
    }
}
