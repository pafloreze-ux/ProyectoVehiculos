package com.proyecto.vehiculos.service;

import com.proyecto.vehiculos.dto.DocumentoAsociadoDTO;
import com.proyecto.vehiculos.model.DocumentoParametrico;
import com.proyecto.vehiculos.model.Vehiculo;
import com.proyecto.vehiculos.model.VehiculoDocumento;
import com.proyecto.vehiculos.repository.DocumentoParametricoRepository;
import com.proyecto.vehiculos.repository.VehiculoDocumentoRepository;
import com.proyecto.vehiculos.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoDocumentoService {

    private final VehiculoDocumentoRepository vehiculoDocumentoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DocumentoParametricoRepository documentoParametricoRepository;

    public VehiculoDocumentoService(VehiculoDocumentoRepository vehiculoDocumentoRepository,
                                    VehiculoRepository vehiculoRepository,
                                    DocumentoParametricoRepository documentoParametricoRepository) {
        this.vehiculoDocumentoRepository = vehiculoDocumentoRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.documentoParametricoRepository = documentoParametricoRepository;
    }

    public VehiculoDocumento agregarDocumentoAVehiculo(Long vehiculoId, DocumentoAsociadoDTO dto) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + vehiculoId));

        DocumentoParametrico docParam = documentoParametricoRepository.findById(dto.getDocumentoParametricoId())
                .orElseThrow(() -> new RuntimeException("Documento paramétrico no encontrado con ID: " + dto.getDocumentoParametricoId()));

        VehiculoDocumento nuevoDoc = new VehiculoDocumento();
        nuevoDoc.setVehiculo(vehiculo);
        nuevoDoc.setDocumento(docParam);
        nuevoDoc.setFechaExpedicion(dto.getFechaExpedicion());
        nuevoDoc.setFechaVencimiento(dto.getFechaVencimiento());
        nuevoDoc.setEstado("En Verificación"); // Estado por defecto

        return vehiculoDocumentoRepository.save(nuevoDoc);
    }

    public List<VehiculoDocumento> buscarPorTipoDocumento(Long documentoId) {
        return vehiculoDocumentoRepository.findByDocumentoId(documentoId);
    }

    public List<VehiculoDocumento> buscarPorEstado(String estado) {
        return vehiculoDocumentoRepository.findByEstado(estado);
    }
    public List<VehiculoDocumento> obtenerPorVehiculoId(Long vehiculoId) {
        return vehiculoDocumentoRepository.findByVehiculoId(vehiculoId);
    }

    public VehiculoDocumento cambiarEstado(Long id, String nuevoEstado, String observaciones) {
        VehiculoDocumento doc = vehiculoDocumentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + id));
        doc.setEstado(nuevoEstado);
        doc.setObservaciones(observaciones);
        return vehiculoDocumentoRepository.save(doc);
    }
}
