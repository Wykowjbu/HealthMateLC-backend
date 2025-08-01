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
                // Cho phép các endpoint của servey
                .requestMatchers("/", "/survey.html").permitAll()
                // Cho phép các endpoint authentication
                .requestMatchers("/api/auth/**").permitAll()


                // Cho phép các file static và pages
                .requestMatchers("/", "/index.html", "/login.html", "/manager.html", "/employee.html").permitAll()
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/*.css", "/*.js").permitAll()

                // Cho phép health check
                .requestMatchers("/health/**").permitAll()
                       
                 // Cho phép truy cập ảnh
                 .requestMatchers("/uploads/**").permitAll() 
                                   
                // Các nhóm endpoint từ nhánh dev
                .requestMatchers("/employee/**").permitAll()
                .requestMatchers("/admin/**").permitAll()
                .requestMatchers("/customer-service/**").permitAll()
                .requestMatchers("/manager/**").permitAll()
                .requestMatchers("/payment/**").permitAll()

                // Các request còn lại yêu cầu xác thực
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            // Tắt form login mặc định và HTTP Basic
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
        return http.build();
    }
}
