package com.proyecto.vehiculos.dto;

import com.proyecto.vehiculos.model.Vehiculo;
import com.proyecto.vehiculos.model.VehiculoDocumento;
import lombok.Data;

import java.util.List;

@Data
public class VehiculoRespuestaDTO {
    private Vehiculo vehiculo;
    private List<VehiculoDocumento> documentos;

    public VehiculoRespuestaDTO(Vehiculo vehiculo, List<VehiculoDocumento> documentos) {
        this.vehiculo = vehiculo;
        this.documentos = documentos;
    }
}