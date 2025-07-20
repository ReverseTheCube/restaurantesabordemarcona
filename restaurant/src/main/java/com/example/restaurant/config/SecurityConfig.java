package com.example.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/*
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/api/auth/**", "/css/**", "/js/**", "/images/**").permitAll() // Rutas públicas
                .anyRequest().authenticated() // Todas las demás requieren autenticación
            )
            .formLogin(form -> form
                .loginPage("/login") // Ruta personalizada para login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
*/