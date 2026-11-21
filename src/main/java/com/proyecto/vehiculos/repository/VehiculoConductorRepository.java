package com.proyecto.vehiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.vehiculos.model.VehiculoConductor;
import com.proyecto.vehiculos.model.VehiculoConductorPK;
import java.util.List;

@Repository
public interface VehiculoConductorRepository extends JpaRepository<VehiculoConductor, VehiculoConductorPK> {
    List<VehiculoConductor> findByEstado(String estado);
}