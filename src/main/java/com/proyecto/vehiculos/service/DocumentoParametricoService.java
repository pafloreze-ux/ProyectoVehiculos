package com.proyecto.vehiculos.service;

import com.proyecto.vehiculos.model.DocumentoParametrico;
import com.proyecto.vehiculos.repository.DocumentoParametricoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentoParametricoService {

    private final DocumentoParametricoRepository repository;

    public DocumentoParametricoService(DocumentoParametricoRepository repository) {
        this.repository = repository;
    }

    public List<DocumentoParametrico> obtenerTodos() {
        return repository.findAll();
    }

    public Optional<DocumentoParametrico> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    public DocumentoParametrico guardar(DocumentoParametrico documento) {
        return repository.save(documento);
    }

    public DocumentoParametrico actualizar(Long id, DocumentoParametrico documentoActualizado) {
        return repository.findById(id).map(doc -> {
            doc.setCodigo(documentoActualizado.getCodigo());
            doc.setNombre(documentoActualizado.getNombre());
            doc.setTiposVehiculoAplica(documentoActualizado.getTiposVehiculoAplica());
            doc.setObligatorio(documentoActualizado.getObligatorio());
            doc.setDescripcion(documentoActualizado.getDescripcion());
            return repository.save(doc);
        }).orElseThrow(() -> new RuntimeException("Documento paramétrico no encontrado con ID: " + id));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}