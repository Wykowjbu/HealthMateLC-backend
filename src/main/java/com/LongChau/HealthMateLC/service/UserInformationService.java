package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInformationService {

    private final UserInformationRepository userInformationRepository;

    @Autowired
    public UserInformationService(UserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public List<UserInformationDTO> getEmployeeAndManagerByPharmacyId(int pharmacyId) {
        return userInformationRepository.findEmployeeAndManagerByPharmacyId(pharmacyId);
    }
    public UserInformation findUserInformationByUserId(Integer userId) {
        return userInformationRepository.findById(userId).orElse(null);
    }
    public void save(UserInformation userInformation) {
        userInformationRepository.save(userInformation);
    }

    public UserInformation findManagerByPharmacyId(Integer pharmacyId) {
        List<UserInformation> managers = userInformationRepository.findManagersByPharmacyId(pharmacyId);
        return managers.isEmpty() ? null : managers.get(0);
    }

    public List<UserInformation> findManagersByPharmacyId(Integer pharmacyId) {
        return userInformationRepository.findManagersByPharmacyId(pharmacyId);
    }
}
