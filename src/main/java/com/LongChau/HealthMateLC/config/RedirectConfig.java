package com.LongChau.HealthMateLC.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedirectConfig {

    @Value("${app.redirect-urls.admin}")
    private String adminUrl;

    @Value("${app.redirect-urls.manager}")
    private String managerUrl;

    @Value("${app.redirect-urls.pharmacist}")
    private String pharmacistUrl;

    @Value("${app.redirect-urls.default}")
    private String defaultUrl;

    public String getRedirectUrl(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> adminUrl;
            case "manager" -> managerUrl;
            case "pharmacist" -> pharmacistUrl;
            default -> defaultUrl;
        };
    }
}