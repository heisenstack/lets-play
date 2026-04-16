package com.lets_play.lets_play.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — we use stateless JWT, not cookies, so CSRF doesn't apply
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session — Spring Security never creates an HttpSession
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Custom 401 handler — returns JSON instead of a login redirect
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(jwtAuthEntryPoint))

            // Access rules
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints — always public
                .requestMatchers("/auth/**").permitAll()

                // GET /products is public (browsing)
                .requestMatchers(HttpMethod.GET, "/products", "/products/**").permitAll()

                // User management — admins only
                .requestMatchers("/users/**").hasRole("ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Plug in our JWT filter BEFORE Spring's built-in auth filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with default strength 10 — good balance of security vs. performance
        return new BCryptPasswordEncoder();
    }
}