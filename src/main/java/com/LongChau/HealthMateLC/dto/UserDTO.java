package com.LongChau.HealthMateLC.dto;
import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String email;
    private String role;
    private Integer pharmacyId;
    public UserDTO(String username, String password, String fullName, String phone, String email, String role, Integer pharmacyId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.pharmacyId = pharmacyId;
    }
}
