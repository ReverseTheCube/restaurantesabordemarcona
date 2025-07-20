package com.example.restaurant.entidades;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Cliente")
    private Integer idCliente;

    @Column(name = "Nombre_Cliente", length = 100)
    private String nombreCliente; // Para clientes particulares

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo_Cliente", nullable = false, length = 50)
    private TipoCliente tipoCliente;

    // Campos para pensionados (personas naturales)
    @Column(name = "DNI", length = 8)
    private String dni;

    @Column(name = "Nombres", length = 100)
    private String nombres;

    @Column(name = "Apellidos", length = 100) 
    private String apellidos;

    // ✅ RELACIONES CON @JsonIgnore PARA EVITAR CICLOS
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // ← ESTO ROMPE EL CICLO
    private List<Pension> pensiones = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // ← ESTO ROMPE EL CICLO
    private List<Pedido> pedidos = new ArrayList<>();

    // Constructores
    public Cliente() {
    }

    public Cliente(String nombreCliente, TipoCliente tipoCliente) {
        this.nombreCliente = nombreCliente;
        this.tipoCliente = tipoCliente;
    }

    // Constructor para pensionados
    public Cliente(String dni, String nombres, String apellidos) {
        this.dni = dni;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipoCliente = TipoCliente.PENSION;
    }

    // Constructor para particulares
    public Cliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
        this.tipoCliente = TipoCliente.PARTICULAR;
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        if (tipoCliente == TipoCliente.PENSION && nombres != null && apellidos != null) {
            return nombres + " " + apellidos;
        }
        return nombreCliente != null ? nombreCliente : "";
    }

    public String getIdentificacion() {
        if (tipoCliente == TipoCliente.PENSION) {
            return dni;
        }
        return "N/A";
    }

    // Validación personalizada
    public boolean esValido() {
        switch (tipoCliente) {
            case PENSION:
                return dni != null && !dni.isEmpty() && 
                       nombres != null && !nombres.isEmpty() && 
                       apellidos != null && !apellidos.isEmpty();
            case PARTICULAR:
                return nombreCliente != null && !nombreCliente.isEmpty();
            default:
                return false;
        }
    }

    // Getters y Setters
    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public List<Pension> getPensiones() {
        return pensiones;
    }

    public void setPensiones(List<Pension> pensiones) {
        this.pensiones = pensiones;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    // ✅ MÉTODO toString() PARA DEBUGGING
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Cliente{");
        sb.append("idCliente=").append(idCliente);
        sb.append(", tipoCliente=").append(tipoCliente);
        
        if (tipoCliente == TipoCliente.PENSION) {
            sb.append(", dni='").append(dni).append('\'');
            sb.append(", nombres='").append(nombres).append('\'');
            sb.append(", apellidos='").append(apellidos).append('\'');
        } else if (tipoCliente == TipoCliente.PARTICULAR) {
            sb.append(", nombreCliente='").append(nombreCliente).append('\'');
        }
        
        sb.append(", pensiones=").append(pensiones != null ? pensiones.size() : 0);
        sb.append(", pedidos=").append(pedidos != null ? pedidos.size() : 0);
        sb.append('}');
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return idCliente != null && idCliente.equals(cliente.idCliente);
    }

    @Override
    public int hashCode() {
        return idCliente != null ? idCliente.hashCode() : 0;
    }
}