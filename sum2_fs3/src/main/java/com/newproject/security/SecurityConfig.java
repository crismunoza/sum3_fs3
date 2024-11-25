package com.newproject.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtRequestFilter jwtRequestFilter;
    private static final String ROLE_ADMIN = "ADMIN"; // Definir constante
    private static final String ROLE_USER = "USER"; // Definir constante


    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }
  
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:80"));
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando SecurityFilterChain");

        http
                .csrf(csrf -> csrf.disable()) 
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Usa el corsConfigurationSource creado
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso sin autenticación a login, registro y solicitudes OPTIONS
                        .requestMatchers("/api/auth/login", "/api/usuarios/register").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                        .requestMatchers("/images/**").permitAll()
                        // Reglas específicas para usuarios y administradores
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/{username}").hasAnyRole(ROLE_USER, ROLE_ADMIN) 
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/{id}").hasAnyRole(ROLE_USER, ROLE_ADMIN) 
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/{id}").hasAnyRole(ROLE_USER, ROLE_ADMIN) 
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole(ROLE_ADMIN) 

                        // Rutas para productos
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll() 
                        .requestMatchers(HttpMethod.POST, "/api/productos").hasRole(ROLE_ADMIN) 
                        .requestMatchers(HttpMethod.PUT, "/api/productos/{id}").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/{id}").hasRole(ROLE_ADMIN)

                        // Cualquier otra solicitud necesita autenticación
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
