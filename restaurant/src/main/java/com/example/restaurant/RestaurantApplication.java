package com.example.restaurant;

import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class RestaurantApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}

	/**
	 * Este Bean se ejecuta al iniciar la aplicación para precargar la base de datos.
	 * Usamos @Profile("!test") para que no se ejecute durante las pruebas automáticas.
	 */
	@Bean
	@Profile("!test")
	CommandLineRunner initDatabase(
			UsuarioRepository usuarioRepository,
			ClienteRepository clienteRepository,
			ComidaRepository comidaRepository,
			BebidaRepository bebidaRepository,
			PedidoRepository pedidoRepository
	) {
		return args -> {
			// Solo se ejecuta si la base de datos está completamente vacía
			if (usuarioRepository.count() > 0) {
				System.out.println("La base de datos ya contiene datos. No se realizará la precarga.");
				return;
			}

			System.out.println(">>> Iniciando precarga de datos de ejemplo...");

			// 1. Crear y guardar entidades independientes
			System.out.println("Creando usuarios y clientes...");

			// CORRECCIÓN: Se usa el constructor vacío y los setters existentes en la entidad Usuario.
			// Se establece el rol como lo requiere la entidad y se elimina la llamada a un método inexistente.
			Usuario mesero = new Usuario();
			mesero.setNombreUsuario("carlos_perez");
			mesero.setContrasena("pass123");
			mesero.setRol(Rol.EMPLOYED); // Usando el enum Rol
			usuarioRepository.save(mesero);

			// CORRECCIÓN: Se usa un valor válido del enum TipoCliente (ej. PARTICULAR).
			// Asumimos que Cliente tiene un constructor (String, TipoCliente) o setters.
			// Para máxima compatibilidad, usamos setters si el constructor no estuviera definido.
			Cliente cliente = new Cliente();
			cliente.setNombreCliente("Valeria Mendoza");
			cliente.setTipoCliente(TipoCliente.PARTICULAR); // Usando un valor válido del enum
			clienteRepository.save(cliente);


			System.out.println("Creando menú de comidas y bebidas...");
			// CORRECCIÓN: Se usan los nombres de enum en minúsculas como están definidos en tus archivos.
			Comida lomo = new Comida();
			lomo.setNombreComida("Lomo Saltado");
			lomo.setTipoComida(TipoComida.plato); // Valor en minúscula
			lomo.setPrecio(new BigDecimal("38.00"));

			Comida causa = new Comida();
			causa.setNombreComida("Causa Rellena de Pollo");
			causa.setTipoComida(TipoComida.entrada); // Valor en minúscula
			causa.setPrecio(new BigDecimal("25.50"));
			comidaRepository.saveAll(List.of(lomo, causa));

			Bebida incaKola = new Bebida();
			incaKola.setNombreBebida("Inca Kola 500ml");
			incaKola.setTipoBebida(TipoBebida.gaseosa); // Valor en minúscula
			incaKola.setPrecio(new BigDecimal("5.00"));

			Bebida chicha = new Bebida();
			chicha.setNombreBebida("Chicha Morada (Jarra)");
			chicha.setTipoBebida(TipoBebida.jugo); // Valor en minúscula
			chicha.setPrecio(new BigDecimal("15.00"));
			bebidaRepository.saveAll(List.of(incaKola, chicha));

			// 2. Crear un pedido completo que relacione todo
			System.out.println("Creando un pedido de ejemplo completo...");
			Pedido pedidoEjemplo = new Pedido();
			pedidoEjemplo.setCliente(cliente); // Usa la instancia recién creada
			pedidoEjemplo.setUsuario(mesero);  // Usa la instancia recién creada
			pedidoEjemplo.setFecha(LocalDateTime.now().minusDays(1));
			pedidoEjemplo.setEstado("PAGADO");

			// 3. Crear los detalles del pedido
			DetallePedido detalle1 = new DetallePedido();
			detalle1.setComida(lomo);
			detalle1.setTipoItem(TipoItem.COMIDA);
			detalle1.setCantidad(2);
			detalle1.setPrecioUnitario(lomo.getPrecio());
			detalle1.setSubtotal(lomo.getPrecio().multiply(new BigDecimal(2)));
			detalle1.setPedido(pedidoEjemplo); // Importante: enlazar el detalle al pedido

			DetallePedido detalle2 = new DetallePedido();
			detalle2.setBebida(incaKola);
			detalle2.setTipoItem(TipoItem.BEBIDA);
			detalle2.setCantidad(3);
			detalle2.setPrecioUnitario(incaKola.getPrecio());
			detalle2.setSubtotal(incaKola.getPrecio().multiply(new BigDecimal(3)));
			detalle2.setPedido(pedidoEjemplo); // Importante: enlazar el detalle al pedido

			// 4. Calcular el total y establecer la relación bidireccional
			BigDecimal totalPedido = detalle1.getSubtotal().add(detalle2.getSubtotal());
			pedidoEjemplo.setTotal(totalPedido);
			pedidoEjemplo.setDetalles(List.of(detalle1, detalle2)); // Añadir los detalles al pedido

			// 5. Guardar el pedido. Gracias a CascadeType.ALL, los detalles se guardarán automáticamente.
			pedidoRepository.save(pedidoEjemplo);

			System.out.println("<<< Precarga de datos completada. Se creó el pedido de ejemplo con ID: " + pedidoEjemplo.getIdPedido());
		};
	}
}