package com.proyecto.vehiculos.repository;

import com.proyecto.vehiculos.model.DocumentoParametrico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentoParametricoRepository extends JpaRepository<DocumentoParametrico, Long> {
    Optional<DocumentoParametrico> findByCodigo(String codigo);
}