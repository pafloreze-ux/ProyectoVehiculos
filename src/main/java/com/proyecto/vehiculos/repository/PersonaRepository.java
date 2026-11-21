package com.proyecto.vehiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.proyecto.vehiculos.model.Persona;
import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    List<Persona> findByTipoPersona(String tipoPersona);
    
    @Query("SELECT p.tipoPersona, COUNT(p) FROM Persona p GROUP BY p.tipoPersona")
    List<Object[]> contarPersonasPorTipo();
}