package com.LongChau.HealthMateLC.dto.CustomerService;

public class UserInformationCsDTO {
    private int id;
    private String name;
    private String role;
    private String email;
    private String phone;
    private String username;
    private Boolean isActive;

    
    public UserInformationCsDTO(int id, String name, String role, String email, String phone, String username,
            Boolean isActive) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.isActive = isActive;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }



}
