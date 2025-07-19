package com.LongChau.HealthMateLC.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;

public class AttendanceDTO {
    private Integer timesheetId;
    private Integer userId;
    private String fullName;
    private LocalDate date;
    private LocalTime checkin;
    private LocalTime checkout;
    private String totalHours;

    // Constructors
    public AttendanceDTO() {}

    public AttendanceDTO(Integer timesheetId, Integer userId, String fullName,
                         LocalDate date, LocalTime checkin, LocalTime checkout) {
        this.timesheetId = timesheetId;
        this.userId = userId;
        this.fullName = fullName;
        this.date = date;
        this.checkin = checkin;
        this.checkout = checkout;
        this.calculateTotalHours();
    }

    // Method to calculate total working hours
    private void calculateTotalHours() {
        if (checkin != null && checkout != null) {
            try {
                Duration duration = Duration.between(checkin, checkout);
                if (duration.isNegative()) {
                    // Handle case where checkout is next day
                    duration = duration.plusDays(1);
                }

                long hours = duration.toHours();
                long minutes = duration.toMinutesPart();
                this.totalHours = String.format("%02d:%02d", hours, minutes);
            } catch (Exception e) {
                this.totalHours = "N/A";
            }
        } else {
            this.totalHours = checkout == null ? "Chưa checkout" : "N/A";
        }
    }

    // Getters and Setters
    public Integer getTimesheetId() {
        return timesheetId;
    }

    public void setTimesheetId(Integer timesheetId) {
        this.timesheetId = timesheetId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckin() {
        return checkin;
    }

    public void setCheckin(LocalTime checkin) {
        this.checkin = checkin;
        this.calculateTotalHours();
    }

    public LocalTime getCheckout() {
        return checkout;
    }

    public void setCheckout(LocalTime checkout) {
        this.checkout = checkout;
        this.calculateTotalHours();
    }

    public String getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(String totalHours) {
        this.totalHours = totalHours;
    }
}