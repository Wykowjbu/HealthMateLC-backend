package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
}
