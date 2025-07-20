package com.example.restaurant.Controlador;

import com.example.restaurant.dto.ItemDTO;
import com.example.restaurant.servicio.BebidaService;
import com.example.restaurant.servicio.ComidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/api") // Se establece una ruta base para todo el controlador
public class ProductController {

    private final ComidaService comidaService;
    private final BebidaService bebidaService;

    /**
     * MEJORA: Se utiliza inyección por constructor.
     * Es la forma recomendada por Spring para inyectar dependencias.
     * Es más seguro y facilita las pruebas.
     */
    @Autowired
    public ProductController(ComidaService comidaService, BebidaService bebidaService) {
        this.comidaService = comidaService;
        this.bebidaService = bebidaService;
    }

    /**
     * Endpoint de API unificado que devuelve todos los productos (comidas y bebidas)
     * como una sola lista de Items para que el frontend (platos.html) los consuma.
     * @return Una respuesta HTTP 200 OK con la lista de todos los items.
     */
    @GetMapping("/items")
    @ResponseBody // Esencial para devolver JSON en lugar de una vista HTML
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        // 1. Convertir todas las Comidas a ItemDTO
        List<ItemDTO> comidasDTO = comidaService.listarComidas().stream()
                .map(comida -> new ItemDTO(
                        comida.getIdComida(),
                        comida.getNombreComida(),
                        comida.getPrecio(),
                        comida.getTipoComida().name().toLowerCase() // "plato" o "entrada"
                ))
                .collect(Collectors.toList());

        // 2. Convertir todas las Bebidas a ItemDTO
        List<ItemDTO> bebidasDTO = bebidaService.listarBebidas().stream()
                .map(bebida -> new ItemDTO(
                        bebida.getIdBebida(),
                        bebida.getNombreBebida(),
                        bebida.getPrecio(),
                        bebida.getTipoBebida().name().toLowerCase(), // "gaseosa" o "jugo"
                        true // Sobrecarga para identificar que es bebida y asignar tipo="bebida"
                ))
                .collect(Collectors.toList());

        // 3. Combinar ambas listas en una sola para enviarla al frontend
        List<ItemDTO> itemsCombinados = Stream.concat(comidasDTO.stream(), bebidasDTO.stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(itemsCombinados);
    }

    // NOTA: Se han eliminado los métodos duplicados y los que renderizaban vistas
    // (como /platos, /bebidas), ya que tu frontend ahora es estático y consume
    // datos únicamente a través del endpoint de API /api/items.
    // Esto hace que el controlador sea más limpio y enfocado en su propósito.
}