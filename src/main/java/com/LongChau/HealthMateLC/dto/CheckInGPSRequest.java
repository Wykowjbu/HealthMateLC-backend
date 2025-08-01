package com.LongChau.HealthMateLC.dto;

import lombok.Data;

@Data
public class CheckInGPSRequest {
    private Integer scheduleId;
    private Double lat;
    private Double lng;
}