package com.LongChau.HealthMateLC.dto;

import lombok.Data;

@Data
public class PharmacyDTO {
    private Integer pharmacyId;
    private String pharmacyName;
    private String address;
    private String phone;
    private String email;

    public PharmacyDTO() {
    }

    public PharmacyDTO(Integer pharmacyId, String pharmacyName, String address, String phone, String email) {
        this.pharmacyId = pharmacyId;
        this.pharmacyName = pharmacyName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
}
