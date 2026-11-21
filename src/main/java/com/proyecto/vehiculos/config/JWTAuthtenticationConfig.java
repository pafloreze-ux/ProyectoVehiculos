package com.proyecto.vehiculos.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import static com.proyecto.vehiculos.config.Constans.*;

@Configuration
public class JWTAuthtenticationConfig {

    public String getJWTToken(String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN");

        String token = Jwts.builder()
                .setId("ProyectoVehiculos_JWT")
                .setSubject(username)
                .claim("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(SUPER_SECRET_KEY), SignatureAlgorithm.HS512)
                .compact();

        return "Bearer " + token;
    }
}