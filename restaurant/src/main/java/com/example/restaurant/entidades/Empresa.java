package com.example.restaurant.entidades;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa")
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Empresa")
    private Integer idEmpresa;

    @Column(name = "RUC", nullable = false, unique = true, length = 11)
    private String ruc;

    @Column(name = "Razon_Social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "Fecha_Registro")
    private LocalDate fechaRegistro;

    @Column(name = "Activo")
    private Boolean activo;

    // ✅ RELACIÓN CON @JsonIgnore PARA EVITAR CICLOS
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // ← ESTO ROMPE EL CICLO
    private List<Pension> pensiones = new ArrayList<>();

    // Constructores
    public Empresa() {
        this.fechaRegistro = LocalDate.now();
        this.activo = true;
    }

    public Empresa(String ruc, String razonSocial) {
        this();
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }

    // Métodos de utilidad
    public boolean esValida() {
        return ruc != null && ruc.matches("\\d{11}") && 
               razonSocial != null && !razonSocial.trim().isEmpty();
    }

    public int getCantidadPensiones() {
        return pensiones != null ? pensiones.size() : 0;
    }

    public String getEstado() {
        return activo ? "Activa" : "Inactiva";
    }

    // Getters y Setters
    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Pension> getPensiones() {
        return pensiones;
    }

    public void setPensiones(List<Pension> pensiones) {
        this.pensiones = pensiones;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "idEmpresa=" + idEmpresa +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", activo=" + activo +
                ", pensiones=" + getCantidadPensiones() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empresa)) return false;
        Empresa empresa = (Empresa) o;
        return ruc != null && ruc.equals(empresa.ruc);
    }

    @Override
    public int hashCode() {
        return ruc != null ? ruc.hashCode() : 0;
    }
}