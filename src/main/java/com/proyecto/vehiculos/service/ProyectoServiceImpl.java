package com.proyecto.vehiculos.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proyecto.vehiculos.dto.DocumentoPdfDTO;
import com.proyecto.vehiculos.model.*;
import com.proyecto.vehiculos.repository.*;

@Service
public class ProyectoServiceImpl implements UserDetailsService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VehiculoConductorRepository vehiculoConductorRepository;

    @Autowired
    private VehiculoDocumentoRepository vehiculoDocumentoRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    // Cargar Usuario para Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario appUser = usuarioRepository.findByLogin(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return new User(appUser.getId().getLogin(), appUser.getPassword(), new ArrayList<>());
    }

    // Requerimiento 1: Crear Persona + Usuario automático si es A (Administrativo)
    public Persona guardarPersona(Persona persona) {
        if (!"A".equals(persona.getTipoPersona()) && !"C".equals(persona.getTipoPersona())) {
            throw new RuntimeException("El tipo de persona debe ser 'A' (Administrativo) o 'C' (Conductor)");
        }

        Persona nuevaPersona = personaRepository.save(persona);

        if ("A".equals(nuevaPersona.getTipoPersona())) {
            // Regla nemotécnica: Primera letra nombre + Primera letra apellido + Identificación
            String pNombre = nuevaPersona.getNombres().substring(0, 1).toLowerCase();
            String pApellido = nuevaPersona.getApellidos().substring(0, 1).toLowerCase();
            String login = pNombre + pApellido + nuevaPersona.getIdentificacion();

            // Password y APIKey generados de forma automática
            String passwordAuto = UUID.randomUUID().toString().substring(0, 8);
            String apiKeyAuto = UUID.randomUUID().toString().replace("-", "");

            UsuarioPK pk = new UsuarioPK(login, nuevaPersona.getId());
            Usuario usuario = new Usuario();
            usuario.setId(pk);
            usuario.setPassword(passwordAuto);
            usuario.setApikey(apiKeyAuto);

            usuarioRepository.save(usuario);
        }

        return nuevaPersona;
    }

    // Requerimiento 2: Cambio de password
    public boolean cambiarPassword(String login, String newPassword) {
        Usuario usr = usuarioRepository.findByLogin(login);
        if (usr != null) {
            usr.setPassword(newPassword);
            usuarioRepository.save(usr);
            return true;
        }
        return false;
    }

    // Requerimiento 3: Regeneración de APIKey
    public String regenerarApiKey(String login) {
        Usuario usr = usuarioRepository.findByLogin(login);
        if (usr != null) {
            String nuevaApiKey = UUID.randomUUID().toString().replace("-", "");
            usr.setApikey(nuevaApiKey);
            usuarioRepository.save(usr);
            return nuevaApiKey;
        }
        return null;
    }

    // Requerimiento 4: Cargue/Actualización masiva de documentos PDF Base64
    public void cargarDocumentosPdf(List<DocumentoPdfDTO> listaDtos) {
        for (DocumentoPdfDTO dto : listaDtos) {
            var docOpt = vehiculoDocumentoRepository.findById(dto.getIdDocumento());
            if (docOpt.isPresent()) {
                VehiculoDocumento doc = docOpt.get();
                if (dto.getPdfBase64() != null) {
                    doc.setDocumento_pdf(dto.getPdfBase64().getBytes());
                    vehiculoDocumentoRepository.save(doc);
                }
            }
        }
    }

    // Requerimiento 5: Asociar vehículos a Conductor (Solo tipo 'C')
    public VehiculoConductor asociarVehiculoConductor(Long vehiculoId, Long personaId) {
        Persona p = personaRepository.findById(personaId).orElse(null);
        if (p == null || !"C".equals(p.getTipoPersona())) {
            throw new RuntimeException("Solo se pueden asociar personas de tipo CONDUCTOR ('C')");
        }

        VehiculoConductorPK pk = new VehiculoConductorPK(vehiculoId, personaId);
        VehiculoConductor vc = new VehiculoConductor();
        vc.setId(pk);
        vc.setFechaAsociacion(LocalDate.now());
        vc.setEstado("EA"); // Por defecto Espera de Aprobación

        return vehiculoConductorRepository.save(vc);
    }

    // Requerimiento 6: Cambiar estado Conductor en Vehículo ('PO', 'EA', 'RO')
    public boolean cambiarEstadoConductor(Long vehiculoId, Long personaId, String nuevoEstado) {
        VehiculoConductorPK pk = new VehiculoConductorPK(vehiculoId, personaId);
        VehiculoConductor vc = vehiculoConductorRepository.findById(pk).orElse(null);
        if (vc != null) {
            vc.setEstado(nuevoEstado);
            vehiculoConductorRepository.save(vc);
            return true;
        }
        return false;
    }

    // --- REQUERIMIENTO 7: Servicios Públicos (No requieren Token) ---

    // 7.1 Consultar conductores que puedan operar ('PO')
    public List<VehiculoConductor> obtenerConductoresPO() {
        return vehiculoConductorRepository.findByEstado("PO");
    }

    // 7.2 Total de personas agrupadas por tipo
    public List<Object[]> contarPersonasPorTipo() {
        return personaRepository.contarPersonasPorTipo();
    }

    // 7.3 Consultar todos los vehículos con documentos vencidos
    public List<VehiculoDocumento> obtenerDocumentosVencidos() {
        return vehiculoDocumentoRepository.findAll().stream()
                .filter(d -> d.getFechaVencimiento() != null && d.getFechaVencimiento().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
    }

    // 7.4 Consultar vehículos con documentos por vencer en X días
    public List<VehiculoDocumento> obtenerDocumentosPorVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        return vehiculoDocumentoRepository.findAll().stream()
                .filter(d -> d.getFechaVencimiento() != null 
                          && !d.getFechaVencimiento().isBefore(hoy) 
                          && !d.getFechaVencimiento().isAfter(limite))
                .collect(Collectors.toList());
    }
    
 // 7.5 Consultar un vehículo por placa con sus conductores y documentos
    public Vehiculo obtenerVehiculoPorPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa).orElse(null);
    }
}