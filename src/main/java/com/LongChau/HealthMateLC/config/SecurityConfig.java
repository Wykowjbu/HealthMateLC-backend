package com.LongChau.HealthMateLC.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Cho phép các endpoint authentication
                        .requestMatchers("/api/auth/**").permitAll()

                        // Cho phép các file static và pages
                        .requestMatchers("/", "/index.html", "/login.html", "/manager.html", "/employee.html").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/*.css", "/*.js").permitAll()

                        // Cho phép health check
                        .requestMatchers("/health/**").permitAll()

                        // Tạm thời cho phép tất cả manager endpoints để test
                        .requestMatchers("/manager/**").permitAll()
                        .requestMatchers("/employee/**").permitAll()

                        // Cho phép tất cả còn lại (tạm thời)
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .formLogin(form -> form.disable()) // Disable default login form
                .httpBasic(basic -> basic.disable()); // Disable HTTP Basic auth

        return http.build();
    }
}