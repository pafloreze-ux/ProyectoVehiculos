package com.proyecto.vehiculos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_vehiculo", nullable = false, length = 20)
    private String tipoVehiculo; // 'Automóvil' o 'Motocicleta'

    @Column(nullable = false, unique = true, length = 6)
    private String placa;

    @Column(name = "tipo_servicio", nullable = false, length = 2)
    private String tipoServicio; // 'Pu' o 'Pr'

    @Column(name = "tipo_combustible", nullable = false, length = 20)
    private String tipoCombustible; // 'Gasolina', 'Gas', 'Diesel'

    @Column(name = "capacidad_pasajeros", nullable = false)
    private Integer capacidadPasajeros;

    @Column(nullable = false, length = 7)
    private String color; // Código Hexadecimal (ej: #FF0000)

    @Column(nullable = false)
    private Integer modelo;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String linea;
}