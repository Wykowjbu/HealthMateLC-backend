package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.CheckInGPSRequest;
import com.LongChau.HealthMateLC.model.Timesheet;
import com.LongChau.HealthMateLC.repository.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class TimesheetService {
    @Autowired
    private PharmacyService pharmacyService; // Thêm này nếu cần

    @Autowired
    private UserService userService;

    @Autowired
    private TimesheetRepository timesheetRepository;

    public void checkInWithGPS(CheckInGPSRequest request, Principal principal) {
        // Logic check-in và lưu GPS
        Timesheet timesheet = new Timesheet();
        // Set các thông tin cần thiết
        timesheet.setCheckinLat(request.getLat());
        timesheet.setCheckinLng(request.getLng());
        timesheet.setCheckin(LocalTime.now());
        timesheet.setDate(LocalDate.now());

        timesheetRepository.save(timesheet);
    }
}
