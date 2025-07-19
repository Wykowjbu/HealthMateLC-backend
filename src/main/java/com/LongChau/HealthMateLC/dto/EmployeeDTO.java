package com.LongChau.HealthMateLC.dto;

public class EmployeeDTO {
    private Integer userId;
    private String fullName;
    private String username;

    // Constructors
    public EmployeeDTO() {}

    public EmployeeDTO(Integer userId, String fullName, String username) {
        this.userId = userId;
        this.fullName = fullName;
        this.username = username;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}