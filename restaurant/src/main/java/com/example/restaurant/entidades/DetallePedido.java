package com.example.restaurant.entidades;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "DetallePedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")
    private Integer idDetallePedido;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_comida")
    private Comida comida;

    @ManyToOne
    @JoinColumn(name = "id_bebida")
    private Bebida bebida;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal subtotal;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('COMIDA', 'BEBIDA')")
    private TipoItem tipoItem;

    @PrePersist
    @PreUpdate
    private void calcularSubtotal() {
    	this.subtotal = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }

	public Integer getIdDetallePedido() {
		return idDetallePedido;
	}

	public void setIdDetallePedido(Integer idDetallePedido) {
		this.idDetallePedido = idDetallePedido;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public Comida getComida() {
		return comida;
	}

	public void setComida(Comida comida) {
		this.comida = comida;
	}

	public Bebida getBebida() {
		return bebida;
	}

	public void setBebida(Bebida bebida) {
		this.bebida = bebida;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public TipoItem getTipoItem() {
		return tipoItem;
	}

	public void setTipoItem(TipoItem tipoItem) {
		this.tipoItem = tipoItem;
	}


    
    
}
