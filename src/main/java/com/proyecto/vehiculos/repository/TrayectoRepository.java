package com.proyecto.vehiculos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.vehiculos.model.Trayecto;

@Repository
public interface TrayectoRepository extends JpaRepository<Trayecto, Long> {

    // 1. Obtener trayectos por código de ruta (flexible a mayúsculas/espacios)
    @Query(value = "SELECT * FROM trayecto WHERE TRIM(LOWER(codigo_ruta)) = TRIM(LOWER(:codigoRuta)) ORDER BY orden_parada ASC", nativeQuery = true)
    List<Trayecto> findByCodigoRutaOrderByOrdenParadaAsc(@Param("codigoRuta") String codigoRuta);

    // 2. Rutas por Cédula del Conductor o por ID de Persona
    @Query(value = "SELECT DISTINCT t.codigo_ruta FROM trayecto t JOIN persona p ON t.idpersona = p.id WHERE TRIM(p.Identificacion) = TRIM(:numId) OR CAST(p.id AS CHAR) = TRIM(:numId)", nativeQuery = true)
    List<String> findCodigosRutaByConductor(@Param("numId") String numId);

    // 3. Ruta y Conductor por Placa del Vehículo
    @Query(value = "SELECT DISTINCT t.codigo_ruta, p.Identificacion FROM trayecto t JOIN vehiculo v ON t.idvehiculo = v.id JOIN persona p ON t.idpersona = p.id WHERE TRIM(LOWER(v.placa)) = TRIM(LOWER(:placa))", nativeQuery = true)
    List<Object[]> findRutaYConductorByPlaca(@Param("placa") String placa);

    // 4. Trayectos inhabilitados
    @Query(value = "SELECT t.* FROM trayecto t JOIN persona p ON t.idpersona = p.id WHERE UPPER(p.estado) = 'RO' OR p.fecha_vigencia_licencia < CURRENT_DATE()", nativeQuery = true)
    List<Trayecto> findTrayectosRutasInhabilitadas();

    // 5. Trayectos sin coordenadas para ScheduledTasks
    @Query(value = "SELECT * FROM trayecto WHERE latitud IS NULL OR longitud IS NULL", nativeQuery = true)
    List<Trayecto> findByLatitudIsNullOrLongitudIsNull();
}