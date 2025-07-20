package com.example.restaurant.entidades;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;
import com.example.restaurant.entidades.Rol;

@Entity
@Table(name = "Usuario")
public class Usuario {  // Implementa UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Usuario")
    private Integer idUsuario;

    @Column(name = "Nombre_Usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "Contrasena", nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)  // Usamos Enum en lugar de String
    @Column(name = "Rol", nullable = false)
    private Rol rol;  // Ahora es un Enum

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;

    // ----- Métodos de UserDetails -----
 
    // Getters y Setters (sin cambios, pero ajustados para el Enum)
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    
    public String notfoundRol () {
    	return "ERROR";
    }


		
	}