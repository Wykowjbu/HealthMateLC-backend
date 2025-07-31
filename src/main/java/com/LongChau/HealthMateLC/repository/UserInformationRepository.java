package com.LongChau.HealthMateLC.repository;

import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.model.UserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserInformationRepository extends JpaRepository<UserInformation, Integer> {
    @Query("SELECT new com.LongChau.HealthMateLC.dto.UserInformationDTO(ui.userId, ui.fullName, u.username, ui.phone, ui.email, u.role, ui.pharmacy.pharmacyId, u.isActive)  " +
            "FROM User u inner join UserInformation ui on u.userId = ui.userId inner join Pharmacy p on ui.pharmacy= p where (u.role = 'employee' or u.role = 'manager') and p.pharmacyId = ?1")
    List<UserInformationDTO> findEmployeeAndManagerByPharmacyId(int pharmacyId);

    // Tìm user info theo email
    UserInformation findByEmail(String email);

    // Kiểm tra tồn tại email
    boolean existsByEmail(String email);

    // Lấy danh sách user info theo pharmacyId
    List<UserInformation> findByPharmacyPharmacyId(Integer pharmacyId);

    // Lấy danh sách manager theo pharmacyId
    @Query("SELECT ui FROM UserInformation ui JOIN ui.user u WHERE u.role = 'manager' AND ui.pharmacy.pharmacyId = ?1")
    List<UserInformation> findManagersByPharmacyId(Integer pharmacyId);
}

