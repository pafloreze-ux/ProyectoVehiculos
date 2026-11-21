package com.proyecto.vehiculos.model;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "persona")
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column(name = "tipo_identificacion", nullable = false)
    private String tipoIdentificacion;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false)
    private String email;

    @Column(name = "tipo_persona", nullable = false)
    private String tipoPersona;

    @Column(name = "estado")
    private String estado;

    @Lob
    @Column(name = "licencia_conduccion")
    private String licenciaConduccion;

    @Column(name = "fecha_vigencia_licencia")
    private LocalDate fechaVigenciaLicencia;

    public Persona() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLicenciaConduccion() {
        return licenciaConduccion;
    }

    public void setLicenciaConduccion(String licenciaConduccion) {
        this.licenciaConduccion = licenciaConduccion;
    }

    public LocalDate getFechaVigenciaLicencia() {
        return fechaVigenciaLicencia;
    }

    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }
}