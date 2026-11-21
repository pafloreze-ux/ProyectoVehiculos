package com.proyecto.vehiculos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trayecto_tmp")
public class TrayectoTmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identificacion_conductor")
    private String identificacionConductor;

    @Column(name = "placa_vehiculo")
    private String placaVehiculo;

    @Column(name = "codigo_ruta")
    private String codigoRuta;

    private String ubicacion;

    @Column(name = "orden_parada")
    private String ordenParada;

    private String latitud;
    private String longitud;

    @Column(name = "usuario_registro")
    private String usuarioRegistro;

    private String estado;
    private String observacion;

    @Column(name = "id_cargue")
    private Long idCargue;

}