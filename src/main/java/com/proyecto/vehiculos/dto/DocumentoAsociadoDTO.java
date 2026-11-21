package com.proyecto.vehiculos.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentoAsociadoDTO {
    private Long documentoParametricoId;
    private LocalDate fechaExpedicion;
    private LocalDate fechaVencimiento;
}