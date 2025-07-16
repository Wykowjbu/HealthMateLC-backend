package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.dto.EmployeeInfoDTO;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.repository.UserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public EmployeeInfoDTO getEmployeeInfoById(Integer userId) {
        Optional<UserInformation> userInfoOptional = userInformationRepository.findById(userId);

        if (userInfoOptional.isPresent()) {
            UserInformation userInfo = userInfoOptional.get();
            String pharmacyName = userInfo.getPharmacy() != null ? userInfo.getPharmacy().getPharmacyName() : null;

            return new EmployeeInfoDTO(
                    userInfo.getUserId(),
                    userInfo.getFullName(),
                    userInfo.getPhone(),
                    userInfo.getEmail(),
                    pharmacyName,
                    userInfo.getAssignedDate()
            );
        }

        return null;
    }
}
