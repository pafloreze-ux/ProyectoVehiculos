package com.proyecto.vehiculos.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.vehiculos.model.Trayecto;
import com.proyecto.vehiculos.repository.TrayectoRepository;

@RestController
@RequestMapping("/api/trayectos")
@CrossOrigin(origins = "*")
public class TrayectoController {

    @Autowired
    private TrayectoRepository trayectoRepository;

    // 1. Endpoint para buscar trayectos por código de ruta
    @GetMapping("/ruta/{codigoRuta}")
    public ResponseEntity<List<Trayecto>> obtenerPorRuta(@PathVariable String codigoRuta) {
        List<Trayecto> trayectos = trayectoRepository.findByCodigoRutaOrderByOrdenParadaAsc(codigoRuta);
        return ResponseEntity.ok(trayectos != null ? trayectos : Collections.emptyList());
    }

    // 2. Endpoint para buscar códigos de ruta por cédula de conductor
    @GetMapping("/conductor/{numId}")
    public ResponseEntity<List<String>> obtenerPorConductor(@PathVariable String numId) {
        List<String> rutas = trayectoRepository.findCodigosRutaByConductor(numId);
        return ResponseEntity.ok(rutas != null ? rutas : Collections.emptyList());
    }

    // 3. Endpoint para buscar ruta y conductor por placa del vehículo
    @GetMapping("/vehiculo/{placa}")
    public ResponseEntity<List<Object[]>> obtenerPorVehiculo(@PathVariable String placa) {
        List<Object[]> res = trayectoRepository.findRutaYConductorByPlaca(placa);
        return ResponseEntity.ok(res != null ? res : Collections.emptyList());
    }

    // 4. Endpoint para obtener rutas inhabilitadas
    @GetMapping("/inhabilitados")
    public ResponseEntity<List<Trayecto>> obtenerInhabilitados() {
        List<Trayecto> inhabilitados = trayectoRepository.findTrayectosRutasInhabilitadas();
        return ResponseEntity.ok(inhabilitados != null ? inhabilitados : Collections.emptyList());
    }
}