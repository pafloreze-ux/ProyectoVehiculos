package com.proyecto.vehiculos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "documento_parametrico")
public class DocumentoParametrico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "tipos_vehiculo_aplica", nullable = false, length = 2)
    private String tiposVehiculoAplica; // 'A', 'M', 'AM'

    @Column(nullable = false, length = 2)
    private String obligatorio; // 'RA', 'RM', 'RR'

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}