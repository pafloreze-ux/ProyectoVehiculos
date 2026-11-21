package com.proyecto.vehiculos.service;

import com.proyecto.vehiculos.dto.DocumentoAsociadoDTO;
import com.proyecto.vehiculos.dto.VehiculoRegistroDTO;
import com.proyecto.vehiculos.dto.VehiculoRespuestaDTO;
import com.proyecto.vehiculos.model.DocumentoParametrico;
import com.proyecto.vehiculos.model.Vehiculo;
import com.proyecto.vehiculos.model.VehiculoDocumento;
import com.proyecto.vehiculos.repository.DocumentoParametricoRepository;
import com.proyecto.vehiculos.repository.VehiculoDocumentoRepository;
import com.proyecto.vehiculos.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final DocumentoParametricoRepository documentoRepository;
    private final VehiculoDocumentoRepository vehiculoDocumentoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository,
                           DocumentoParametricoRepository documentoRepository,
                           VehiculoDocumentoRepository vehiculoDocumentoRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.documentoRepository = documentoRepository;
        this.vehiculoDocumentoRepository = vehiculoDocumentoRepository;
    }

    @Transactional
    public VehiculoRespuestaDTO crearVehiculo(VehiculoRegistroDTO registroDTO) {
        // Regla 1: No se puede crear un vehículo sin un documento asociado
        if (registroDTO.getDocumentos() == null || registroDTO.getDocumentos().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear un vehículo sin al menos un documento asociado.");
        }

        // Regla 2: Validación estricta del formato de placa según tipo
        validarPlaca(registroDTO.getTipoVehiculo(), registroDTO.getPlaca());

        // Guardar la entidad Vehiculo
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setTipoVehiculo(registroDTO.getTipoVehiculo());
        vehiculo.setPlaca(registroDTO.getPlaca().toUpperCase());
        vehiculo.setTipoServicio(registroDTO.getTipoServicio());
        vehiculo.setTipoCombustible(registroDTO.getTipoCombustible());
        vehiculo.setCapacidadPasajeros(registroDTO.getCapacidadPasajeros());
        vehiculo.setColor(registroDTO.getColor());
        vehiculo.setModelo(registroDTO.getModelo());
        vehiculo.setMarca(registroDTO.getMarca());
        vehiculo.setLinea(registroDTO.getLinea());

        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        // Asociar documentos con estado inicial "En Verificación"
        for (DocumentoAsociadoDTO docDTO : registroDTO.getDocumentos()) {
            DocumentoParametrico docParam = documentoRepository.findById(docDTO.getDocumentoParametricoId())
                    .orElseThrow(() -> new IllegalArgumentException("Documento paramétrico no encontrado con ID: " + docDTO.getDocumentoParametricoId()));

            VehiculoDocumento vDoc = new VehiculoDocumento();
            vDoc.setVehiculo(vehiculoGuardado);
            vDoc.setDocumento(docParam);
            vDoc.setFechaExpedicion(docDTO.getFechaExpedicion());
            vDoc.setFechaVencimiento(docDTO.getFechaVencimiento());
            vDoc.setEstado("En Verificación"); // Estado obligatorio inicial

            vehiculoDocumentoRepository.save(vDoc);
        }

        List<VehiculoDocumento> documentosGuardados = vehiculoDocumentoRepository.findByVehiculoId(vehiculoGuardado.getId());
        return new VehiculoRespuestaDTO(vehiculoGuardado, documentosGuardados);
    }

    private void validarPlaca(String tipoVehiculo, String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("La placa es obligatoria.");
        }
        
        String placaUpper = placa.toUpperCase();

        if ("Automóvil".equalsIgnoreCase(tipoVehiculo) || "Automovil".equalsIgnoreCase(tipoVehiculo)) {
            // 3 letras seguidas de 3 números (Ej: ABC123)
            if (!placaUpper.matches("^[A-Z]{3}[0-9]{3}$")) {
                throw new IllegalArgumentException("La placa para Automóvil debe tener 3 letras seguidas de 3 números (ej. ABC123).");
            }
        } else if ("Motocicleta".equalsIgnoreCase(tipoVehiculo)) {
            // 3 letras, 2 números y 1 letra final (Ej: ABC12A)
            if (!placaUpper.matches("^[A-Z]{3}[0-9]{2}[A-Z]{1}$")) {
                throw new IllegalArgumentException("La placa para Motocicleta debe contener 3 letras, 2 números y 1 letra al final (ej. ABC12A).");
            }
        } else {
            throw new IllegalArgumentException("Tipo de vehículo no válido. Utilice 'Automóvil' o 'Motocicleta'.");
        }
    }

    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll();
    }

    public Optional<VehiculoRespuestaDTO> obtenerPorId(Long id) {
        return vehiculoRepository.findById(id).map(vehiculo -> {
            List<VehiculoDocumento> docs = vehiculoDocumentoRepository.findByVehiculoId(vehiculo.getId());
            return new VehiculoRespuestaDTO(vehiculo, docs);
        });
    }

    public Optional<VehiculoRespuestaDTO> buscarPorPlaca(String placa) {
        return vehiculoRepository.findByPlaca(placa.toUpperCase()).map(vehiculo -> {
            List<VehiculoDocumento> docs = vehiculoDocumentoRepository.findByVehiculoId(vehiculo.getId());
            return new VehiculoRespuestaDTO(vehiculo, docs);
        });
    }

    public List<Vehiculo> buscarPorTipoVehiculo(String tipoVehiculo) {
        return vehiculoRepository.findByTipoVehiculo(tipoVehiculo);
    }

    public Vehiculo actualizar(Long id, Vehiculo vehiculoDetalles) {
        return vehiculoRepository.findById(id).map(v -> {
            validarPlaca(vehiculoDetalles.getTipoVehiculo(), vehiculoDetalles.getPlaca());
            v.setTipoVehiculo(vehiculoDetalles.getTipoVehiculo());
            v.setPlaca(vehiculoDetalles.getPlaca().toUpperCase());
            v.setTipoServicio(vehiculoDetalles.getTipoServicio());
            v.setTipoCombustible(vehiculoDetalles.getTipoCombustible());
            v.setCapacidadPasajeros(vehiculoDetalles.getCapacidadPasajeros());
            v.setColor(vehiculoDetalles.getColor());
            v.setModelo(vehiculoDetalles.getModelo());
            v.setMarca(vehiculoDetalles.getMarca());
            v.setLinea(vehiculoDetalles.getLinea());
            return vehiculoRepository.save(v);
        }).orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        List<VehiculoDocumento> documentos = vehiculoDocumentoRepository.findByVehiculoId(id);
        vehiculoDocumentoRepository.deleteAll(documentos);
        vehiculoRepository.deleteById(id);
    }
}