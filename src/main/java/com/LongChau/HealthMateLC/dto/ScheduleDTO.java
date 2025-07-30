package com.LongChau.HealthMateLC.dto;

public class ScheduleDTO {
    private Integer scheduleId;
    private Integer userId;
    private String fullName;
    private String date;
    private String startTime;
    private String endTime;
    private Integer pharmacyId;
    private String pharmacyName;

    // Constructors
    public ScheduleDTO() {}

    public ScheduleDTO(Integer scheduleId, Integer userId, String fullName, String date, String startTime, String endTime, Integer pharmacyId, String pharmacyName) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.fullName = fullName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
    }

    // Getters and Setters
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(Integer pharmacyId) { this.pharmacyId = pharmacyId; }
    public String getPharmacyName() { return pharmacyName; }
    public void setPharmacyName(String pharmacyName) { this.pharmacyName = pharmacyName; }
}