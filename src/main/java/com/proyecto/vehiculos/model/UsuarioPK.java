package com.proyecto.vehiculos.model;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UsuarioPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "login")
    private String login;

    @Column(name = "idpersona")
    private Long idpersona;

    public UsuarioPK() {}
    public UsuarioPK(String login, Long idpersona) {
        this.login = login;
        this.idpersona = idpersona;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public Long getIdpersona() { return idpersona; }
    public void setIdpersona(Long idpersona) { this.idpersona = idpersona; }
}