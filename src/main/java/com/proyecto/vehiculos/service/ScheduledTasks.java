package com.proyecto.vehiculos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.proyecto.vehiculos.model.Persona;
import com.proyecto.vehiculos.model.Trayecto;
import com.proyecto.vehiculos.repository.PersonaRepository;
import com.proyecto.vehiculos.repository.TrayectoRepository;

@Component
public class ScheduledTasks {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private TrayectoRepository trayectoRepository;

    // Tarea 1: Cada minuto - Verificar vigencia de licencias de conductores
    @Scheduled(fixedRate = 5000)
    public void verificarLicenciasConductores() {
        List<Persona> personas = personaRepository.findAll();
        LocalDate hoy = LocalDate.now();

        for (Persona p : personas) {
            if ("C".equalsIgnoreCase(p.getTipoPersona())
                    && p.getFechaVigenciaLicencia() != null
                    && p.getFechaVigenciaLicencia().isBefore(hoy)) {
                
                p.setEstado("RO");
                personaRepository.save(p);
            }
        }
    }

    // Tarea 2: Cada 2 minutos - Asignar coordenadas por defecto a trayectos sin latitud/longitud
    @Scheduled(fixedRate = 5000)
    public void completarCoordenadasFaltantes() {
        List<Trayecto> sinCoordenadas = trayectoRepository.findByLatitudIsNullOrLongitudIsNull();

        for (Trayecto trayecto : sinCoordenadas) {
            if (trayecto.getLatitud() == null || trayecto.getLongitud() == null) {
                trayecto.setLatitud(4.43889);
                trayecto.setLongitud(-75.23222);
                trayectoRepository.save(trayecto);
            }
        }
    }
}