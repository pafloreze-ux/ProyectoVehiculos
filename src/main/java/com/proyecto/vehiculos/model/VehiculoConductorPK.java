package com.proyecto.vehiculos.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class VehiculoConductorPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "vehiculo_id")
    private Long vehiculoId;

    @Column(name = "persona_id")
    private Long personaId;

    public VehiculoConductorPK() {}
    public VehiculoConductorPK(Long vehiculoId, Long personaId) {
        this.vehiculoId = vehiculoId;
        this.personaId = personaId;
    }

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public Long getPersonaId() { return personaId; }
    public void setPersonaId(Long personaId) { this.personaId = personaId; }
}