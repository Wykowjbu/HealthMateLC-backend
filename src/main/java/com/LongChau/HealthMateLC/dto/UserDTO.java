package com.LongChau.HealthMateLC.dto;
import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UserDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 đến 100 ký tự")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải có 10-11 chữ số")
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    @NotBlank(message = "Vai trò không được để trống")
    @Pattern(regexp = "^(employee|manager)$", message = "Vai trò phải là employee hoặc manager hoặc csvc")
    private String role;

    @NotNull(message = "ID nhà thuốc không được để trống")
    @Min(value = 1, message = "ID nhà thuốc phải lớn hơn 0")
    private Integer pharmacyId;

    // Default constructor
    public UserDTO() {}

    // Constructor with parameters
    public UserDTO(String username, String password, String fullName, String phone, String email, String role, Integer pharmacyId) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.pharmacyId = pharmacyId;
    }
}