package com.fujentopj.fujento.module.users.infrastructure.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilita CSRF in modo compatibile con Spring Security 6.1+
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll() // Permette l'accesso a tutte le risorse senza autenticazione
                ).headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
        ;
        return http.build();
    }
}