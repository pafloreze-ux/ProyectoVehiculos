package com.proyecto.vehiculos.repository;

import com.proyecto.vehiculos.model.VehiculoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoDocumentoRepository extends JpaRepository<VehiculoDocumento, Long> {

    // Buscar vehículos que tengan en común un tipo de documento (Requerimiento)
    List<VehiculoDocumento> findByDocumentoId(Long documentoId);

    // Buscar vehículos según el estado del documento ('Habilitado', 'Vencido', 'En Verificación') (Requerimiento)
    List<VehiculoDocumento> findByEstado(String estado);
    
    // Buscar todos los documentos asignados a un vehículo específico
    List<VehiculoDocumento> findByVehiculoId(Long vehiculoId);
}