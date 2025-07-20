package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

	// Este método ya lo tenías y es para el historial de ventas general.
	List<Pedido> findAllByOrderByIdPedidoDesc();

	// --- MÉTODO AÑADIDO PARA CORREGIR EL ERROR ---
	// Spring Data JPA entenderá que debe buscar Pedidos filtrando por
	// el campo 'usuario' y, dentro de este, por su campo 'idUsuario'.
	List<Pedido> findByUsuario_IdUsuario(Integer usuarioId);
}