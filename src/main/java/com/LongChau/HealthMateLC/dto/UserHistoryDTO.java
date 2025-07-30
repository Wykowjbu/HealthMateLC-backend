package com.LongChau.HealthMateLC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHistoryDTO {
    private Integer historyId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String pharmacyName;
}
