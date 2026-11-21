package com.proyecto.vehiculos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.vehiculos.model.TrayectoTmp;

@Repository
public interface TrayectoTmpRepository extends JpaRepository<TrayectoTmp, Long> {

    // Consulta por identificador de cargue (Requerimiento 20% API)
    List<TrayectoTmp> findByIdCargue(Long idCargue);

    @Modifying
    @Transactional
    @Query(value = "CALL sp_cargue_trayecto(:idCond, :placa, :ruta, :ubicacion, :orden, :lat, :lon, :usuario, :idCargue)", nativeQuery = true)
    void ejecutarCargue(
        @Param("idCond") String idCond, @Param("placa") String placa,
        @Param("ruta") String ruta, @Param("ubicacion") String ubicacion,
        @Param("orden") String orden, @Param("lat") String lat,
        @Param("lon") String lon, @Param("usuario") String usuario,
        @Param("idCargue") Long idCargue
    );

    @Modifying
    @Transactional
    @Query(value = "CALL sp_validacion_trayecto(:idCargue)", nativeQuery = true)
    void ejecutarValidacion(@Param("idCargue") Long idCargue);

    @Modifying
    @Transactional
    @Query(value = "CALL sp_procesado_trayecto(:idCargue)", nativeQuery = true)
    void ejecutarProcesado(@Param("idCargue") Long idCargue);
}