package com.proyecto.vehiculos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.proyecto.vehiculos.config.JWTAuthtenticationConfig;
import com.proyecto.vehiculos.config.JwtRequest;
import com.proyecto.vehiculos.config.JwtResponse;
import com.proyecto.vehiculos.model.Usuario;
import com.proyecto.vehiculos.repository.UsuarioRepository;
import com.proyecto.vehiculos.service.ProyectoServiceImpl;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private JWTAuthtenticationConfig jwtAuthtenticationConfig;

    @Autowired
    private ProyectoServiceImpl proyectoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @RequestMapping(
        value = "/authenticate",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtRequest authenticationRequest,
            @RequestHeader(value = "APIKey", required = false) String apiKeyHeader) throws Exception {

        UserDetails userDetails = proyectoService.loadUserByUsername(authenticationRequest.getUsername());

        Usuario usuario = usuarioRepository.findByLogin(authenticationRequest.getUsername());
        if (usuario != null && usuario.getApikey() != null) {
            if (apiKeyHeader == null || !apiKeyHeader.equals(usuario.getApikey())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("APIKey inválida o ausente en el Header");
            }
        }

        final String token = jwtAuthtenticationConfig.getJWTToken(userDetails.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}