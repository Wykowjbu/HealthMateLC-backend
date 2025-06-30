package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInformationRepository extends JpaRepository<UserInformation, Integer> {
    @Query("SELECT new com.LongChau.HealthMateLC.dto.UserInformationDTO(ui.userId, ui.fullName, ui.phone, ui.email, u.role)  " +
            "FROM User u inner join UserInformation ui on u.userId = ui.userId inner join Pharmacy p on ui.pharmacy= p where (u.role = 'employee' or u.role = 'manager') and p.pharmacyId = :pharmacyId")
    List<UserInformationDTO> findEmployeeAndManagerByPharmacyId(int pharmacyId);
    List<UserInformation> findByPharmacyPharmacyId(Integer pharmacyId);
    @Query("SELECT ui FROM UserInformation ui JOIN ui.user u WHERE u.role = 'manager' AND ui.pharmacy.pharmacyId = :pharmacyId")
    List<UserInformation> findManagersByPharmacyId(@Param("pharmacyId") Integer pharmacyId);
}

