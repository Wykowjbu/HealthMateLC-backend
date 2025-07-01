package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.dto.PharmacyDTO;
import com.LongChau.HealthMateLC.dto.UserInformationDTO;
import com.LongChau.HealthMateLC.dto.UserDTO;
import com.LongChau.HealthMateLC.dto.ProductDTO;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.PharmacyService;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.LongChau.HealthMateLC.service.UserService;
import com.LongChau.HealthMateLC.service.ProductService;
import com.LongChau.HealthMateLC.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "${app.frontend.base-url}")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private PharmacyService pharmacyService;
    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private com.LongChau.HealthMateLC.repository.PharmacyRepository pharmacyRepository;
    @Autowired
    private com.LongChau.HealthMateLC.repository.UserRepository userRepository;
    @Autowired
    private com.LongChau.HealthMateLC.repository.UserInformationRepository userInformationRepository;

    @GetMapping("/list-accounts")
    public ResponseEntity<Map<String, Object>> listAccounts() {
        Map<String, Object> map = new HashMap<>();
        List<Integer> numberList = new ArrayList<>();
        numberList.add(userService.countUserByRole("employee"));
        numberList.add(pharmacyService.getNumberOfPharmacies());
        numberList.add(customerService.getNumberOfCustomers());
        map.put("listNumbers", numberList);

        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        map.put("listPharmacies", pharmacies);

        Map<String, Object> map1 = new HashMap<>();
        pharmacies.forEach(pharmacy -> {
            List<UserInformationDTO> listUser = userInformationService.getEmployeeAndManagerByPharmacyId(pharmacy.getPharmacyId());
            map1.put(String.valueOf(pharmacy.getPharmacyId()), listUser);
        });
        map.put("listUsersByPharmacy", map1);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/list-pharmacies")
    public ResponseEntity<List<Pharmacy>> listPharmacies() {
        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        return new ResponseEntity<>(pharmacies, HttpStatus.OK);
    }

    @GetMapping("/list-pharmacy")
    public ResponseEntity<List<Map<String, Object>>> listPharmaciesWithManager() {
        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Pharmacy p : pharmacies) {
            Map<String, Object> map = new HashMap<>();
            map.put("pharmacyId", p.getPharmacyId());
            map.put("pharmacyName", p.getPharmacyName());
            map.put("address", p.getAddress());
            map.put("phone", p.getPhone());
            map.put("email", p.getEmail());
            map.put("isActive", p.getIsActive());
            // Lấy tất cả manager của nhà thuốc
            List<UserInformation> managers = userInformationService.findManagersByPharmacyId(p.getPharmacyId());
            String managerNames = "";
            if (managers != null && !managers.isEmpty()) {
                List<String> names = new ArrayList<>();
                for (UserInformation manager : managers) {
                    String name = (manager.getFullName() != null && !manager.getFullName().isBlank())
                        ? manager.getFullName() : manager.getUser().getUsername();
                    names.add(name);
                }
                managerNames = String.join(", ", names);
            }
            map.put("manager", managerNames.isEmpty() ? "Chưa gán" : managerNames);
            result.add(map);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/list-roles")
    public ResponseEntity<List<String>> listRoles() {
        List<String> roles = userService.getDistinctRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @PostMapping("/add-account")
    public ResponseEntity<Map<String, String>> addAccount(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        // Check validation errors from Bean Validation
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            response.put("message", errorMessage);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Business logic validation
        if (userService.existsByUsername(userDTO.getUsername())) {
            response.put("message", "Tên đăng nhập đã tồn tại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userService.getDistinctRoles().contains(userDTO.getRole().toLowerCase())) {
            response.put("message", "Vai trò không hợp lệ");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (userDTO.getPharmacyId() != null && !pharmacyService.existsById(userDTO.getPharmacyId())) {
            response.put("message", "Nhà thuốc không tồn tại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Create user
            UserDTO userToSave = new UserDTO(
                    userDTO.getUsername(),
                    userDTO.getPassword(),
                    userDTO.getFullName(),
                    userDTO.getPhone(),
                    userDTO.getEmail(),
                    userDTO.getRole().toLowerCase(),
                    userDTO.getPharmacyId()
            );
            userService.createUser(userToSave);

            // Create user information
            User createdUser = userService.findUserByUsername(userDTO.getUsername());
            if (createdUser != null) {
                UserInformation userInformation = new UserInformation();
                userInformation.setUser(createdUser); // @MapsId sẽ tự động set userId
                userInformation.setFullName(userDTO.getFullName());
                userInformation.setPhone(userDTO.getPhone());
                userInformation.setEmail(userDTO.getEmail());

                // Set pharmacy if provided
                if (userDTO.getPharmacyId() != null) {
                    Optional<Pharmacy> pharmacy = pharmacyService.findById(userDTO.getPharmacyId());
                    if (pharmacy.isPresent()) {
                        userInformation.setPharmacy(pharmacy.get());
                    }
                }

                userInformationService.save(userInformation);
            }

            response.put("message", "Tạo tài khoản thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-product")
    public ResponseEntity<Map<String, String>> addProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        // Check validation errors from Bean Validation
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            response.put("message", errorMessage);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Create product using service
            Product createdProduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(Map.of("message", "Thêm sản phẩm thành công"));
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("message", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list-products")
    public ResponseEntity<List<ProductDTO>> listProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductDTO dto = new ProductDTO();
            dto.setProductName(p.getProductName());
            dto.setProductType(p.getProductType());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            dtos.add(dto);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/create-pharmacy")
    public ResponseEntity<?> createPharmacy(@Valid @RequestBody PharmacyDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }
        // Kiểm tra trùng tên nhà thuốc
        if (pharmacyService.existsByPharmacyName(dto.getPharmacyName())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tên nhà thuốc đã tồn tại"));
        }
        // Kiểm tra trùng số điện thoại
        if (pharmacyService.existsByPhone(dto.getPhone())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã tồn tại"));
        }
        // Kiểm tra trùng email (nếu có nhập)
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && pharmacyService.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email đã tồn tại"));
        }
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setPharmacyName(dto.getPharmacyName());
        pharmacy.setAddress(dto.getAddress());
        pharmacy.setPhone(dto.getPhone());
        pharmacy.setEmail(dto.getEmail());
        pharmacy.setIsActive(true);
        pharmacy.setCreatedDate(LocalDateTime.now());
        pharmacyRepository.save(pharmacy);
        return ResponseEntity.ok(Map.of("message", "Tạo nhà thuốc thành công"));
    }

    @PutMapping("/update-pharmacy/{id}")
    public ResponseEntity<?> updatePharmacy(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        Optional<Pharmacy> optionalPharmacy = pharmacyService.findById(id);
        if (optionalPharmacy.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nhà thuốc không tồn tại"));
        }
        Pharmacy pharmacy = optionalPharmacy.get();

        // Nếu chỉ cập nhật trạng thái
        if (updates.containsKey("isActive") && updates.size() == 1) {
            pharmacy.setIsActive((Boolean) updates.get("isActive"));
            pharmacyRepository.save(pharmacy);
            return ResponseEntity.ok(Map.of("message", "Cập nhật trạng thái thành công"));
        }

        // Nếu cập nhật thông tin khác, validate như cũ
        String pharmacyName = (String) updates.get("pharmacyName");
        String address = (String) updates.get("address");
        String phone = (String) updates.get("phone");
        String email = (String) updates.get("email");

        // Validate trùng tên, số điện thoại, email
        if (pharmacyName != null && !pharmacy.getPharmacyName().equals(pharmacyName) && pharmacyService.existsByPharmacyName(pharmacyName)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tên nhà thuốc đã tồn tại"));
        }
        if (phone != null && !pharmacy.getPhone().equals(phone) && pharmacyService.existsByPhone(phone)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã tồn tại"));
        }
        if (email != null && !email.isBlank() && !email.equals(pharmacy.getEmail()) && pharmacyService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email đã tồn tại"));
        }

        // Cập nhật thông tin
        if (pharmacyName != null) pharmacy.setPharmacyName(pharmacyName);
        if (address != null) pharmacy.setAddress(address);
        if (phone != null) pharmacy.setPhone(phone);
        if (email != null) pharmacy.setEmail(email);

        pharmacyRepository.save(pharmacy);
        return ResponseEntity.ok(Map.of("message", "Cập nhật nhà thuốc thành công"));
    }

    @GetMapping("/pharmacy/{id}")
    public ResponseEntity<Pharmacy> getPharmacy(@PathVariable Integer id) {
        Optional<Pharmacy> pharmacy = pharmacyService.findById(id);
        return pharmacy.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }
}