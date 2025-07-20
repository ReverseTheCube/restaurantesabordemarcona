package com.example.restaurant.entidades;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Insumo")
public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Integer idInsumo;

    @Column(name = "nombre_insumo", nullable = false)
    private String nombreInsumo;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "insumo", cascade = CascadeType.ALL)
    private List<DetalleCompra> compras;

	public Integer getIdInsumo() {
		return idInsumo;
	}

	public void setIdInsumo(Integer idInsumo) {
		this.idInsumo = idInsumo;
	}

	public String getNombreInsumo() {
		return nombreInsumo;
	}

	public void setNombreInsumo(String nombreInsumo) {
		this.nombreInsumo = nombreInsumo;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public List<DetalleCompra> getCompras() {
		return compras;
	}

	public void setCompras(List<DetalleCompra> compras) {
		this.compras = compras;
	}


    
}
