package com.proyecto.vehiculos.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.vehiculos.model.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // Retorna Optional para ser compatible con el método buscarPorPlaca de VehiculoService
    Optional<Vehiculo> findByPlaca(String placa);

    // Método para buscar lista de vehículos por tipo
    List<Vehiculo> findByTipoVehiculo(String tipoVehiculo);
}