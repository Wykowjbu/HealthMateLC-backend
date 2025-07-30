package com.LongChau.HealthMateLC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/employee/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/customer-service/**").permitAll()
                        .requestMatchers("/manager/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()  // Cho phép truy cập ảnh
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}