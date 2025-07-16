package com.LongChau.HealthMateLC.dto;
import lombok.Data;


@Data
public class UserDTO {
    private String username;
    private String password;
    private String role;
    private Boolean isActive;
    public UserDTO() {}
    public UserDTO(String username, String password, String role, Boolean isActive) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }
}
