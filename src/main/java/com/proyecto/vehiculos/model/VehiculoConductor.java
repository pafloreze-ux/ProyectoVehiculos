package com.proyecto.vehiculos.model;

import java.io.Serializable;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "vehiculo_conductor")
public class VehiculoConductor implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private VehiculoConductorPK id;

    @Column(name = "fecha_asociacion", nullable = false)
    private LocalDate fechaAsociacion;

    @Column(nullable = false)
    private String estado;

    public VehiculoConductor() {}

    public VehiculoConductorPK getId() { return id; }
    public void setId(VehiculoConductorPK id) { this.id = id; }
    public LocalDate getFechaAsociacion() { return fechaAsociacion; }
    public void setFechaAsociacion(LocalDate fechaAsociacion) { this.fechaAsociacion = fechaAsociacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}