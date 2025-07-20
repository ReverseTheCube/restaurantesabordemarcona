package com.example.restaurant.entidades;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "Comida")
public class Comida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comida")
    private Integer idComida;

    @Column(name = "nombre_comida", nullable = false)
    private String nombreComida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comida", nullable = false)
    private TipoComida tipoComida;

    @Column(name = "precio", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal precio;

	public Integer getIdComida() {
		return idComida;
	}

	public void setIdComida(Integer idComida) {
		this.idComida = idComida;
	}

	public String getNombreComida() {
		return nombreComida;
	}

	public void setNombreComida(String nombreComida) {
		this.nombreComida = nombreComida;
	}

	public TipoComida getTipoComida() {
		return tipoComida;
	}

	public void setTipoComida(TipoComida tipoComida) {
		this.tipoComida = tipoComida;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}


    
}
