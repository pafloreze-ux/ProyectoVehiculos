package com.proyecto.vehiculos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Autowired
    private JWTAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Permitir explícitamente la autenticación
                .requestMatchers("/authenticate").permitAll()
                // Permitir servicios públicos
                .requestMatchers("/publico/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Permitir acceso a la interfaz web y endpoints de trayectos
                .requestMatchers("/", "/index.html", "/static/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/trayectos/**").permitAll()
                .anyRequest().authenticated()
            )
            // Aplicar el filtro de JWT
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}