package com.fujentopj.fujento.module.users.infrastructure.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Abilita CORS
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**")) // Disabilita CSRF per semplificare lo sviluppo
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()// Permette l'accesso alla console H2 senza autenticazione
                        .anyRequest().permitAll() // Permette l'accesso a tutte le risorse senza autenticazione
                ).headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Specifica le origini consentite (app Flutter)
        // Per lo sviluppo, puoi usare "*" per consentire tutte le origini,
        // ma per la produzione dovresti specificare l'URL esatto della tua app.
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:51544")); // Esempio: "http://localhost:5000" o l'IP del tuo dispositivo/emulatore
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Metodi HTTP consentiti
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")); // Header consentiti
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")); // Header esposti al client
        configuration.setAllowCredentials(true); // Permetti l'invio di credenziali (es. cookie)
        configuration.setMaxAge(3600L); // Tempo massimo (in secondi) per cui le risposte pre-flight possono essere memorizzate nella cache del browser

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applica questa configurazione CORS a tutti i percorsi
        return source;
    }
}