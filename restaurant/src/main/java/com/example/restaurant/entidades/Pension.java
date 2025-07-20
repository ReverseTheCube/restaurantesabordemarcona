package com.example.restaurant.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pension")
public class Pension {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Pension")
    private Integer idPension;

    // ✅ SIN @JsonIgnore: Necesitamos esta info en el frontend
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Cliente", nullable = false)
    private Cliente cliente;

    // ✅ SIN @JsonIgnore: Necesitamos esta info en el frontend  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Empresa", nullable = true)
    private Empresa empresa;

    @Column(name = "Fecha_Inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "Fecha_Fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "Monto_Mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoMensual;

    // Constructores
    public Pension() {
    }

    public Pension(Cliente cliente, Empresa empresa, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal montoMensual) {
        this.cliente = cliente;
        this.empresa = empresa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.montoMensual = montoMensual;
    }

    // Métodos de utilidad
    public boolean estaActiva() {
        LocalDate hoy = LocalDate.now();
        return hoy.isAfter(fechaInicio.minusDays(1)) && hoy.isBefore(fechaFin.plusDays(1));
    }

    public long getDiasRestantes() {
        LocalDate hoy = LocalDate.now();
        if (hoy.isAfter(fechaFin)) {
            return 0;
        }
        return hoy.until(fechaFin).getDays();
    }

    public String getEstado() {
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(fechaInicio)) {
            return "Pendiente";
        } else if (estaActiva()) {
            return "Activa";
        } else {
            return "Vencida";
        }
    }

    public BigDecimal getMontoTotal() {
        if (fechaInicio == null || fechaFin == null || montoMensual == null) {
            return BigDecimal.ZERO;
        }
        long meses = fechaInicio.until(fechaFin).toTotalMonths();
        return montoMensual.multiply(BigDecimal.valueOf(meses));
    }

    public boolean esValida() {
        return cliente != null && 
               fechaInicio != null && 
               fechaFin != null && 
               montoMensual != null && 
               montoMensual.compareTo(BigDecimal.ZERO) > 0 &&
               fechaFin.isAfter(fechaInicio);
    }

    // Getters y Setters
    public Integer getIdPension() {
        return idPension;
    }

    public void setIdPension(Integer idPension) {
        this.idPension = idPension;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    @Override
    public String toString() {
        return "Pension{" +
                "idPension=" + idPension +
                ", cliente=" + (cliente != null ? cliente.getNombreCompleto() : "null") +
                ", empresa=" + (empresa != null ? empresa.getRazonSocial() : "Sin empresa") +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", montoMensual=" + montoMensual +
                ", estado='" + getEstado() + '\'' +
                '}';
    }
}