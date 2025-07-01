package com.LongChau.HealthMateLC.service;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.model.Pharmacy;
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

    public void saveUserInformation(UserInformation userInformation) {
        userInformationRepository.save(userInformation);
    }

    // Tạo UserInformation từ DTO, User, Pharmacy
    public UserInformation createUserInformation(UserInformationDTO dto, User user, Pharmacy pharmacy) {
        UserInformation info = new UserInformation();
        info.setUser(user);
        info.setFullName(dto.getFullName());
        info.setPhone(dto.getPhone());
        info.setEmail(dto.getEmail());
        info.setPharmacy(pharmacy);
        return userInformationRepository.save(info);
    }

    public UserInformation findManagerByPharmacyId(Integer pharmacyId) {
        List<UserInformation> managers = userInformationRepository.findManagersByPharmacyId(pharmacyId);
        return managers.isEmpty() ? null : managers.get(0);
    }

    public List<UserInformation> findManagersByPharmacyId(Integer pharmacyId) {
        return userInformationRepository.findManagersByPharmacyId(pharmacyId);
    }

    public boolean existsByEmail(String email) {
        return userInformationRepository.existsByEmail(email);
    }
}
