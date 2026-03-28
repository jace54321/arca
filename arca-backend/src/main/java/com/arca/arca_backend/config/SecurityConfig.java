package com.arca.arca_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configure Spring Security to:
     * 1. Use JWT validation against Supabase JWKS
     * 2. Enable CORS for Vite dev server
     * 3. Use stateless session (no cookies)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no auth required
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        
                        // All other endpoints require valid JWT
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                );
        
        return http.build();
    }
    
    /**
     * JWT Decoder configured for Supabase JWKS endpoint
     * Extracts the issuer URL from environment variable SUPABASE_PROJECT_URL
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // Get Supabase project URL from environment
        String supabaseProjectUrl = System.getenv("SUPABASE_PROJECT_URL");
        if (supabaseProjectUrl == null || supabaseProjectUrl.isEmpty()) {
            // Fallback to property
            supabaseProjectUrl = "https://xupeembqwzmrpkoegnhr.supabase.co";
        }
        
        // Supabase JWKS endpoint
        String jwksUri = supabaseProjectUrl + "/.well-known/jwks.json";
        
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }
    
    /**
     * CORS configuration allowing requests from Vite dev server
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow requests from Vite dev server
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",    // Vite default port
                "http://localhost:3000",    // Fallback dev port
                "http://127.0.0.1:5173"
        ));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Cache CORS preflight for 1 hour
        configuration.setMaxAge(3600L);
        
        // Expose Authorization header in response
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
