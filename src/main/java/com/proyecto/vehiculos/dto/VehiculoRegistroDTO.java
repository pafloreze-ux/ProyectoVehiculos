package com.proyecto.vehiculos.dto;

import lombok.Data;
import java.util.List;

@Data
public class VehiculoRegistroDTO {
    private String tipoVehiculo; // 'Automóvil' o 'Motocicleta'
    private String placa;
    private String tipoServicio; // 'Pu' o 'Pr'
    private String tipoCombustible; // 'Gasolina', 'Gas', 'Diesel'
    private Integer capacidadPasajeros;
    private String color; // Hexadecimal ej: #FF0000
    private Integer modelo;
    private String marca;
    private String linea;
    
    // Lista obligatoria de documentos con los que se crea el vehículo
    private List<DocumentoAsociadoDTO> documentos;
}