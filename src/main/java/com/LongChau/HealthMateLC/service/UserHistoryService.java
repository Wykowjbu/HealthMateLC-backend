package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.UserHistoryDTO;
import com.LongChau.HealthMateLC.model.UserHistory;
import com.LongChau.HealthMateLC.repository.UserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    public List<UserHistoryDTO> getUserWorkHistoryByUserId(Integer userId) {
        List<UserHistory> userHistories = userHistoryRepository.findByUserIdOrderByStartTimeDesc(userId);

        return userHistories.stream()
                .map(uh -> new UserHistoryDTO(
                    uh.getHistoryId(),
                    uh.getStartTime(),
                    uh.getEndTime(),
                    uh.getPharmacy().getPharmacyName()
                ))
                .collect(Collectors.toList());
    }
}
