package com.LongChau.HealthMateLC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfoDTO {
    private Integer userId;
    private String fullName;
    private String phone;
    private String email;
    private String pharmacyName;
    private LocalDateTime assignedDate;
}
