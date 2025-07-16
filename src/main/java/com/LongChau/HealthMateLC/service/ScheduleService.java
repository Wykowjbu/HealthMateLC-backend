package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.model.Schedule;
import com.LongChau.HealthMateLC.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getById(Integer id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> getSchedulesByUserId(Integer userId) {
        return scheduleRepository.findByUserId(userId);
    }

    public List<Schedule> getSchedulesByWorkDate(LocalDate workDate) {
        return scheduleRepository.findByWorkDate(workDate);
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public void deleteById(Integer id) {
        scheduleRepository.deleteById(id);
    }
}
