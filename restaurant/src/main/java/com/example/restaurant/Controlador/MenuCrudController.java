package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.BebidaRepository;
import com.example.restaurant.repositorios.ComidaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/menu-crud")
public class MenuCrudController {

    private final ComidaRepository comidaRepository;
    private final BebidaRepository bebidaRepository;

    @Autowired
    public MenuCrudController(ComidaRepository comidaRepository, BebidaRepository bebidaRepository) {
        this.comidaRepository = comidaRepository;
        this.bebidaRepository = bebidaRepository;
    }

    @GetMapping
    public String mostrarMenu(@RequestParam(value = "tab", required = false, defaultValue = "comidas") String tab, Model model) {
        model.addAttribute("tab", tab);
        
        if (tab.equals("comidas")) {
            List<Comida> platos = comidaRepository.findByTipoComida(TipoComida.plato);
            model.addAttribute("comidas", platos != null ? platos : Collections.emptyList());
        } else if (tab.equals("entradas")) {
            List<Comida> entradas = comidaRepository.findByTipoComida(TipoComida.entrada);
            model.addAttribute("comidas", entradas != null ? entradas : Collections.emptyList());
        } else if (tab.equals("bebidas")) {
            List<Bebida> bebidas = bebidaRepository.findAll();
            model.addAttribute("bebidas", bebidas != null ? bebidas : Collections.emptyList());
        }

        return "menu-crud";
    }

 // ======= POST: Guardar Comida =======
    @PostMapping("/guardarComida")
    @Transactional
    public String guardarComida(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "nombre") String nombre,
            @RequestParam(name = "precio") String precioStr,
            @RequestParam(name = "tipoComida", required = false) String tipoComidaStr, // Hacerlo opcional
            RedirectAttributes redirectAttributes) {

        try {
            // Validar nombre
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "El nombre no puede estar vacío");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/menu-crud?tab=comidas";
            }
            
            // Convertir precio
            BigDecimal precio = new BigDecimal(precioStr.replace(',', '.'));

            // Validar tipo de comida
            if (tipoComidaStr == null || tipoComidaStr.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Tipo de comida no especificado");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/menu-crud?tab=comidas";
            }
            
            TipoComida tipoComida = TipoComida.valueOf(tipoComidaStr);

            Comida comida;
            if (id != null && id > 0) {
                // Modo edición
                comida = comidaRepository.findById(id)
                         .orElseThrow(() -> new EntityNotFoundException("Comida no encontrada"));
            } else {
                // Modo creación
                comida = new Comida();
            }

            comida.setNombreComida(nombre);
            comida.setPrecio(precio);
            comida.setTipoComida(tipoComida);

            comidaRepository.save(comida);
            
            redirectAttributes.addFlashAttribute("message", "Comida guardada exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Formato de precio inválido");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "Tipo de comida inválido");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        String tabRedirect = (tipoComidaStr != null && "entrada".equals(tipoComidaStr)) ? "entradas" : "comidas";
        return "redirect:/menu-crud?tab=" + tabRedirect;
    }

    // ======= POST: Guardar Bebida =======
    @PostMapping("/guardarBebida")
    @Transactional
    public String guardarBebida(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "nombre") String nombre,
            @RequestParam(name = "precio") String precioStr,
            @RequestParam(name = "tipoBebida", required = false) String tipoBebidaStr, // Hacerlo opcional
            RedirectAttributes redirectAttributes) {

        try {
            // Validar nombre
            if (nombre == null || nombre.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "El nombre no puede estar vacío");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/menu-crud?tab=bebidas";
            }
            
            // Convertir precio
            BigDecimal precio = new BigDecimal(precioStr.replace(',', '.'));

            // Validar tipo de bebida
            if (tipoBebidaStr == null || tipoBebidaStr.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Tipo de bebida no especificado");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/menu-crud?tab=bebidas";
            }
            
            TipoBebida tipoBebida = TipoBebida.valueOf(tipoBebidaStr);

            Bebida bebida;
            if (id != null && id > 0) {
                // Modo edición - Buscar por ID
                bebida = bebidaRepository.findById(id)
                         .orElseThrow(() -> new EntityNotFoundException("Bebida no encontrada"));
            } else {
                // Modo creación
                bebida = new Bebida();
            }

            bebida.setNombreBebida(nombre);
            bebida.setPrecio(precio);
            bebida.setTipoBebida(tipoBebida);

            bebidaRepository.save(bebida);
            
            redirectAttributes.addFlashAttribute("message", "Bebida guardada exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("message", "Formato de precio inválido");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "Tipo de bebida inválido");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/menu-crud?tab=bebidas";
    }
    
	    
	 // === EDITAR COMIDA ===
	    @GetMapping("/comida/editar/{id}")
	    @ResponseBody
	    public Comida obtenerComida(@PathVariable("id") Integer id) {
	        return comidaRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Comida no encontrada con ID: " + id));
	    }
	
	    // === EDITAR BEBIDA ===
	    @GetMapping("/bebida/editar/{id}")
	    @ResponseBody
	    public Bebida obtenerBebida(@PathVariable("id") Integer id) {
	        return bebidaRepository.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Bebida no encontrada con ID: " + id));
	    }
	
	    // === ELIMINAR COMIDA ===
	    @PostMapping("/comida/eliminar/{id}")
	    public String eliminarComida(@PathVariable("id") Integer id) {
	        comidaRepository.deleteById(id);
	        return "redirect:/menu-crud?tab=comidas"; // O entradas, si deseas lógica extra
	    }
	
	    // === ELIMINAR BEBIDA ===
	    @PostMapping("/bebida/eliminar/{id}")
	    public String eliminarBebida(@PathVariable("id") Integer id) {
	        bebidaRepository.deleteById(id);
	        return "redirect:/menu-crud?tab=bebidas";
	    }
    
}
