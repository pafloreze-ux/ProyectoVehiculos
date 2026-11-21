package com.proyecto.vehiculos.controller;

import com.proyecto.vehiculos.model.VehiculoDocumento;
import com.proyecto.vehiculos.service.VehiculoDocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculo-documentos")
public class VehiculoDocumentoController {

    private final VehiculoDocumentoService service;

    public VehiculoDocumentoController(VehiculoDocumentoService service) {
        this.service = service;
    }

    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<VehiculoDocumento>> obtenerPorVehiculo(@PathVariable Long vehiculoId) {
        return ResponseEntity.ok(service.obtenerPorVehiculoId(vehiculoId));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String observaciones) {
        try {
            VehiculoDocumento actualizado = service.cambiarEstado(id, nuevoEstado, observaciones);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}