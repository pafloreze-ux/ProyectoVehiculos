package com.proyecto.vehiculos.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.vehiculos.model.Persona;
import com.proyecto.vehiculos.model.Trayecto;
import com.proyecto.vehiculos.model.Vehiculo;
import com.proyecto.vehiculos.model.VehiculoDocumento;
import com.proyecto.vehiculos.repository.PersonaRepository;
import com.proyecto.vehiculos.repository.TrayectoRepository;
import com.proyecto.vehiculos.repository.VehiculoDocumentoRepository;
import com.proyecto.vehiculos.repository.VehiculoRepository;

@Service
public class TrayectoService {

    @Autowired
    private TrayectoRepository trayectoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    public Trayecto crearTrayecto(Trayecto trayecto) throws Exception {
        // 1. Validar conductor (Tipo 'C' y no en 'RO')
        Persona conductor = personaRepository.findById(trayecto.getConductor().getId())
                .orElseThrow(() -> new Exception("El conductor especificado no existe."));

        if (!"C".equalsIgnoreCase(conductor.getTipoPersona())) {
            throw new Exception("La persona asignada al trayecto debe ser de tipo Conductor ('C').");
        }

        if ("RO".equalsIgnoreCase(conductor.getEstado())) {
            throw new Exception("El conductor se encuentra Restringido para Operar (RO).");
        }

        // 2. Validar vehículo y que sus documentos estén en estado 'Habilitado'
        Vehiculo vehiculo = vehiculoRepository.findById(trayecto.getVehiculo().getId())
                .orElseThrow(() -> new Exception("El vehículo especificado no existe."));

        List<VehiculoDocumento> documentos = vehiculoDocumentoRepository.findAll();
        for (VehiculoDocumento doc : documentos) {
            if (doc.getVehiculo() != null && doc.getVehiculo().getId().equals(vehiculo.getId())) {
                if ("Vencido".equalsIgnoreCase(doc.getEstado()) || "Inhabilitado".equalsIgnoreCase(doc.getEstado())) {
                    throw new Exception("El vehículo no puede realizar el trayecto porque tiene documentos vencidos o inhabilitados.");
                }
            }
        }

        // 3. Guardar trayecto
        trayecto.setConductor(conductor);
        trayecto.setVehiculo(vehiculo);
        return trayectoRepository.save(trayecto);
    }
}