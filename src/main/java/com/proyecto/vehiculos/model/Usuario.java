package com.proyecto.vehiculos.model;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private UsuarioPK id;

    @Column(nullable = false)
    private String password;

    @Column
    private String apikey;

    public Usuario() {}

    public UsuarioPK getId() { return id; }
    public void setId(UsuarioPK id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getApikey() { return apikey; }
    public void setApikey(String apikey) { this.apikey = apikey; }
}