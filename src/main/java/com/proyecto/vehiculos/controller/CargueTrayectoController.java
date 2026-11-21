package com.proyecto.vehiculos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.proyecto.vehiculos.model.TrayectoTmp;
import com.proyecto.vehiculos.service.CargueTrayectoService;

@RestController
@RequestMapping("/api/cargue-trayectos")
@CrossOrigin(origins = "*")
public class CargueTrayectoController {

    @Autowired
    private CargueTrayectoService cargueService;

    // Servicio 1: Cargar Excel (80% ponderación API)
    @PostMapping("/excel")
    public ResponseEntity<?> cargarExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe adjuntar un archivo Excel válido.");
            }
            Long idCargue = cargueService.procesarArchivoExcel(file);
            List<TrayectoTmp> resultados = cargueService.consultarPorIdCargue(idCargue);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Archivo procesado correctamente");
            response.put("idCargue", idCargue);
            response.put("detalleRegistros", resultados);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar el archivo: " + e.getMessage());
        }
    }

    // Servicio 2: Consultar cargue por identificador (20% ponderación API)
    @GetMapping("/{idCargue}")
    public ResponseEntity<List<TrayectoTmp>> consultarCargue(@PathVariable Long idCargue) {
        return ResponseEntity.ok(cargueService.consultarPorIdCargue(idCargue));
    }
}