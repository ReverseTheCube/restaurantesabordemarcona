package com.example.restaurant.entidades;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "Bebida")
public class Bebida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bebida")
    private Integer idBebida;

    @Column(name = "nombre_bebida", nullable = false)
    private String nombreBebida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_bebida", nullable = false)
    private TipoBebida tipoBebida;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

	public Integer getIdBebida() {
		return idBebida;
	}

	public void setIdBebida(Integer idBebida) {
		this.idBebida = idBebida;
	}

	public String getNombreBebida() {
		return nombreBebida;
	}

	public void setNombreBebida(String nombreBebida) {
		this.nombreBebida = nombreBebida;
	}

	public TipoBebida getTipoBebida() {
		return tipoBebida;
	}

	public void setTipoBebida(TipoBebida tipoBebida) {
		this.tipoBebida = tipoBebida;
	}

	public BigDecimal getPrecio() {
        return precio;
    }

	public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    
}
