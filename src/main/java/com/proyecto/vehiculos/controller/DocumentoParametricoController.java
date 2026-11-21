package com.proyecto.vehiculos.controller;

import com.proyecto.vehiculos.model.DocumentoParametrico;
import com.proyecto.vehiculos.service.DocumentoParametricoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos-parametricos")
public class DocumentoParametricoController {

    private final DocumentoParametricoService service;

    public DocumentoParametricoController(DocumentoParametricoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<DocumentoParametrico>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoParametrico> obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DocumentoParametrico> guardar(@RequestBody DocumentoParametrico documento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(documento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentoParametrico> actualizar(@PathVariable Long id, @RequestBody DocumentoParametrico documento) {
        try {
            return ResponseEntity.ok(service.actualizar(id, documento));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}