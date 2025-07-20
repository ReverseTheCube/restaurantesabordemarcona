// V3
package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Insumo;
import com.example.restaurant.repositorios.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final InsumoRepository insumoRepository;

    @Autowired
    public InventarioController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @GetMapping
    public String mostrarInventario(Model model) {
        List<Insumo> insumos = insumoRepository.findAll();
        model.addAttribute("insumos", insumos);
        return "inventario";
    }

    @PostMapping("/actualizar")
    public String actualizarStock(@RequestParam("id") Integer id,
                                 @RequestParam("cantidad") Integer cantidad,
                                 RedirectAttributes redirectAttributes) {
        Insumo insumo = insumoRepository.findById(id).orElse(null);
        
        if (insumo != null) {
            insumo.setStock(cantidad);
            insumoRepository.save(insumo);
            redirectAttributes.addFlashAttribute("success", "Stock actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Insumo no encontrado");
        }
        
        return "redirect:/inventario";
    }
    
    @PostMapping("/agregar")
    public String agregarInsumo(@RequestParam("nombre") String nombre,
                                @RequestParam("stock") Integer stock,
                                RedirectAttributes redirectAttributes) {
        if (nombre == null || nombre.trim().isEmpty() || stock == null || stock < 0) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos");
            return "redirect:/compras";
        }

        Insumo nuevo = new Insumo();
        nuevo.setNombreInsumo(nombre.trim());
        nuevo.setStock(stock);
        insumoRepository.save(nuevo);

        redirectAttributes.addFlashAttribute("success", "Insumo agregado correctamente");
        return "redirect:/compras";
    }
    
    @PostMapping("/eliminar")
    public String eliminarInsumo(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (insumoRepository.existsById(id)) {
            insumoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Insumo eliminado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "El insumo no existe");
        }
        return "redirect:/compras";
    }
}

