// Archivo: src/main/java/com/example/restaurant/dto/PensionDTO.java

package com.example.restaurant.dto;

import com.example.restaurant.entidades.Pension;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PensionDTO {

    private Integer idPension;
    private String clienteNombre;
    private String empresaRazonSocial;
    private BigDecimal montoMensual;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Constructor para convertir la Entidad a DTO
    public PensionDTO(Pension pension) {
        this.idPension = pension.getIdPension();
        this.montoMensual = pension.getMontoMensual();
        this.fechaInicio = pension.getFechaInicio();
        this.fechaFin = pension.getFechaFin();
        
        // Aquí está la magia: accedemos a los datos relacionados MIENTRAS la sesión está activa
        if (pension.getCliente() != null) {
            this.clienteNombre = pension.getCliente().getNombreCompleto();
        }
        if (pension.getEmpresa() != null) {
            this.empresaRazonSocial = pension.getEmpresa().getRazonSocial();
        }
    }

    // Getters y Setters
    public Integer getIdPension() { return idPension; }
    public void setIdPension(Integer idPension) { this.idPension = idPension; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getEmpresaRazonSocial() { return empresaRazonSocial; }
    public void setEmpresaRazonSocial(String empresaRazonSocial) { this.empresaRazonSocial = empresaRazonSocial; }
    public BigDecimal getMontoMensual() { return montoMensual; }
    public void setMontoMensual(BigDecimal montoMensual) { this.montoMensual = montoMensual; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}