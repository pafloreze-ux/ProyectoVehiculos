package com.proyecto.vehiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.proyecto.vehiculos.model.Usuario;
import com.proyecto.vehiculos.model.UsuarioPK;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UsuarioPK> {
    @Query("SELECT u FROM Usuario u WHERE u.id.login = :login")
    Usuario findByLogin(@Param("login") String login);
}