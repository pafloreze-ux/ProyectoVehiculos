package com.proyecto.vehiculos.controller;

import com.proyecto.vehiculos.dto.VehiculoRegistroDTO;
import com.proyecto.vehiculos.dto.VehiculoRespuestaDTO;
import com.proyecto.vehiculos.model.Vehiculo;
import com.proyecto.vehiculos.service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> registrarVehiculo(@RequestBody VehiculoRegistroDTO dto) {
        try {
            VehiculoRespuestaDTO nuevoVehiculo = service.crearVehiculo(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVehiculo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Vehiculo>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoRespuestaDTO> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        }
    }
