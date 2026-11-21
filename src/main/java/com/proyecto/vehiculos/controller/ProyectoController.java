package com.proyecto.vehiculos.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.vehiculos.dto.DocumentoPdfDTO;
import com.proyecto.vehiculos.model.Persona;
import com.proyecto.vehiculos.model.VehiculoConductor;
import com.proyecto.vehiculos.service.ProyectoServiceImpl;

@RestController
@CrossOrigin
public class ProyectoController {

    @Autowired
    private ProyectoServiceImpl proyectoService;

    // --- REQUERIMIENTOS PROTEGIDOS (Requieren Token Bearer + APIKey) ---

    // 1. Crear Persona (POST) - Genera Usuario si es 'A' (15%)
    @PostMapping("/personas")
    public ResponseEntity<?> guardarPersona(@RequestBody Persona persona) {
        try {
            Persona nueva = proyectoService.guardarPersona(persona);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Cambiar contraseña enviando el login en la URL (10%)
    @PutMapping("/usuario/password/{login}")
    public ResponseEntity<?> cambiarPassword(@PathVariable("login") String login, @RequestBody String newPassword) {
        boolean exito = proyectoService.cambiarPassword(login, newPassword);
        if (exito) return ResponseEntity.ok("Contraseña actualizada con éxito");
        return ResponseEntity.notFound().build();
    }

    // 3. Regenerar APIKey por GET enviando el login (10%)
    @GetMapping("/usuario/apikey/{login}")
    public ResponseEntity<?> regenerarApiKey(@PathVariable("login") String login) {
        String nuevaApiKey = proyectoService.regenerarApiKey(login);
        if (nuevaApiKey != null) return ResponseEntity.ok(nuevaApiKey);
        return ResponseEntity.notFound().build();
    }

    // 4. Cargue/Actualización masiva de documentos PDF en Base64 (20%)
    @PostMapping("/vehiculo/documentos/cargar-pdf")
    public ResponseEntity<?> cargarDocumentosPdf(@RequestBody List<DocumentoPdfDTO> documentos) {
        try {
            proyectoService.cargarDocumentosPdf(documentos);
            return ResponseEntity.ok("Documentos PDF guardados con éxito en la Base de Datos");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar los documentos: " + e.getMessage());
        }
    }

    // 5. Asociar vehículo a conductor (5%)
    @PostMapping("/vehiculo/asociar-conductor")
    public ResponseEntity<?> asociarVehiculoConductor(@RequestParam("vehiculoId") Long vehiculoId, 
                                                      @RequestParam("personaId") Long personaId) {
        try {
            VehiculoConductor vc = proyectoService.asociarVehiculoConductor(vehiculoId, personaId);
            return ResponseEntity.ok(vc);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6. Cambiar estado de conductor en vehículo ('PO', 'EA', 'RO') (5%)
    @PutMapping("/vehiculo/cambiar-estado-conductor")
    public ResponseEntity<?> cambiarEstadoConductor(@RequestParam("vehiculoId") Long vehiculoId,
                                                   @RequestParam("personaId") Long personaId,
                                                   @RequestParam("estado") String estado) {
        boolean exito = proyectoService.cambiarEstadoConductor(vehiculoId, personaId, estado);
        if (exito) return ResponseEntity.ok("Estado actualizado con éxito");
        return ResponseEntity.badRequest().body("No se pudo actualizar el estado");
    }

    // --- REQUERIMIENTOS PÚBLICOS (Punto 7 - No requieren Token) (20%) ---

    // 7.1 Consultar todos los vehículos con documentos vencidos
    @GetMapping("/publico/documentos-vencidos")
    public ResponseEntity<?> obtenerDocumentosVencidos() {
        return ResponseEntity.ok(proyectoService.obtenerDocumentosVencidos());
    }

    // 7.2 Consultar todos los conductores que puedan operar ('PO')
    @GetMapping("/publico/conductores-operativos")
    public ResponseEntity<List<VehiculoConductor>> obtenerConductoresPO() {
        return ResponseEntity.ok(proyectoService.obtenerConductoresPO());
    }

    // 7.3 Consultar un vehículo por placa
    @GetMapping("/publico/vehiculo/{placa}")
    public ResponseEntity<?> obtenerVehiculoPorPlaca(@PathVariable("placa") String placa) {
        var vehiculo = proyectoService.obtenerVehiculoPorPlaca(placa);
        if (vehiculo != null) return ResponseEntity.ok(vehiculo);
        return ResponseEntity.notFound().build();
    }

    // 7.4 Consultar vehículos con documentos por vencer en X días
    @GetMapping("/publico/documentos-por-vencer")
    public ResponseEntity<?> obtenerDocumentosPorVencer(@RequestParam(name = "dias", defaultValue = "30") int dias) {
        return ResponseEntity.ok(proyectoService.obtenerDocumentosPorVencer(dias));
    }

    // 7.5 Consultar el total de personas agrupadas por tipo
    @GetMapping("/publico/personas-por-tipo")
    public ResponseEntity<List<Object[]>> contarPersonasPorTipo() {
        return ResponseEntity.ok(proyectoService.contarPersonasPorTipo());
    }
}