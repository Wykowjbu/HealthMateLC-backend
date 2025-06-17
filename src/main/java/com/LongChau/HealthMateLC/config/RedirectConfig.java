package com.LongChau.HealthMateLC.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedirectConfig {

    @Value("${app.redirect-urls.admin}")
    private String adminUrl;

    @Value("${app.redirect-urls.manager}")
    private String managerUrl;

    @Value("${app.redirect-urls.employee}")
    private String employeeUrl;

    @Value("${app.redirect-urls.customerservice}")
    private String customerserviceUrl;

    @Value("${app.redirect-urls.default}")
    private String defaultUrl;

    public String getRedirectUrl(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> adminUrl;
            case "manager" -> managerUrl;
            case "employee" -> employeeUrl;
            case "customer-service" -> customerserviceUrl;
            default -> defaultUrl;
        };
    }
}