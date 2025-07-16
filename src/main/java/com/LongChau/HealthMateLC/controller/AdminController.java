package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.dto.*;
import com.LongChau.HealthMateLC.model.Pharmacy;
import com.LongChau.HealthMateLC.model.UserInformation;
import com.LongChau.HealthMateLC.model.User;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.service.*;
import com.LongChau.HealthMateLC.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private InventoryService inventoryService;
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

        // SỬA ĐÂY: Chuyển entity thành Map để tránh Hibernate proxy
        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        List<Map<String, Object>> pharmacyMaps = new ArrayList<>();

        for (Pharmacy p : pharmacies) {
            Map<String, Object> pharmacyMap = new HashMap<>();
            pharmacyMap.put("pharmacyId", p.getPharmacyId());
            pharmacyMap.put("pharmacyName", p.getPharmacyName());
            pharmacyMap.put("address", p.getAddress());
            pharmacyMap.put("phone", p.getPhone());
            pharmacyMap.put("email", p.getEmail());
            pharmacyMap.put("isActive", p.getIsActive());
            pharmacyMap.put("createdDate", p.getCreatedDate());
            pharmacyMaps.add(pharmacyMap);
        }

        map.put("listPharmacies", pharmacyMaps); // Dùng Map thay vì entity

        Map<String, Object> map1 = new HashMap<>();
        pharmacies.forEach(pharmacy -> {
            List<UserInformationDTO> listUser = userInformationService.getEmployeeAndManagerByPharmacyId(pharmacy.getPharmacyId());
            map1.put(String.valueOf(pharmacy.getPharmacyId()), listUser);
        });
        map.put("listUsersByPharmacy", map1);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/list-pharmacies")
    public ResponseEntity<List<Map<String, Object>>> listPharmacies() {
        List<Pharmacy> pharmacies = pharmacyService.getAllPharmacies();
        List<Map<String, Object>> pharmacyMaps = new ArrayList<>();

        for (Pharmacy p : pharmacies) {
            Map<String, Object> pharmacyMap = new HashMap<>();
            pharmacyMap.put("pharmacyId", p.getPharmacyId());
            pharmacyMap.put("pharmacyName", p.getPharmacyName());
            pharmacyMap.put("address", p.getAddress());
            pharmacyMap.put("phone", p.getPhone());
            pharmacyMap.put("email", p.getEmail());
            pharmacyMap.put("isActive", p.getIsActive());
            pharmacyMap.put("createdDate", p.getCreatedDate());
            pharmacyMaps.add(pharmacyMap);
        }

        return new ResponseEntity<>(pharmacyMaps, HttpStatus.OK);
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
    public ResponseEntity<Map<String, String>> addAccount(
            @Valid @RequestBody UserAccountFullDTO dto,
            BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        // Check validation errors from Bean Validation
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            response.put("message", errorMessage);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // Check email exists in UserInformation
        if (userInformationService.existsByEmail(dto.getEmail())) {
            response.put("message", "Email đã tồn tại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // Business logic validation
        if (userService.existsByUsername(dto.getUsername())) {
            response.put("message", "Tên đăng nhập đã tồn tại");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userService.getDistinctRoles().contains(dto.getRole().toLowerCase())) {
            response.put("message", "Vai trò không hợp lệ");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        // Nếu có pharmacyId thì kiểm tra tồn tại
        Pharmacy pharmacy = null;
        if (dto.getPharmacyId() != null) {
            Optional<Pharmacy> pharmacyOpt = pharmacyService.findById(dto.getPharmacyId());
            if (pharmacyOpt.isPresent()) {
                pharmacy = pharmacyOpt.get();
            } else {
                response.put("message", "Nhà thuốc không tồn tại");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }

        try {
            // Create user
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(dto.getUsername());
            userDTO.setPassword(dto.getPassword());
            userDTO.setRole(dto.getRole());
            userDTO.setIsActive(dto.getIsActive());
            userService.createUser(userDTO);

            // Create user information
            User createdUser = userService.findUserByUsername(dto.getUsername());
            UserInformationDTO userInformationDTO = new UserInformationDTO();
            userInformationDTO.setFullName(dto.getFullName());
            userInformationDTO.setPhone(dto.getPhone());
            userInformationDTO.setEmail(dto.getEmail());
            userInformationDTO.setRole(dto.getRole());
            userInformationDTO.setPharmacyId(dto.getPharmacyId());
            userInformationService.createUserInformation(userInformationDTO, createdUser, pharmacy);

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
            // Create product using service (service sẽ tự xử lý tồn kho nếu có quantity)
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
            dto.setProductId(p.getProductId());
            dto.setProductName(p.getProductName());
            dto.setProductType(p.getProductType());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            // Thêm số lượng tồn kho
            dto.setQuantity(inventoryService.getProductQuantity(p.getProductId()));
            dtos.add(dto);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Integer id) {
        System.out.println("Getting product with ID: " + id);
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            Product p = product.get();
            System.out.println("Product found: " + p.getProductName());
            ProductDTO dto = new ProductDTO();
            dto.setProductId(p.getProductId());
            dto.setProductName(p.getProductName());
            dto.setProductType(p.getProductType());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            // Thêm số lượng tồn kho
            dto.setQuantity(inventoryService.getProductQuantity(p.getProductId()));
            return ResponseEntity.ok(dto);
        }
        System.out.println("Product not found with ID: " + id);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/edit-product/{id}")
    public ResponseEntity<Map<String, String>> updateProduct(@PathVariable Integer id,
                                                             @Valid @RequestBody ProductDTO productDTO,
                                                             BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        // Check validation errors from Bean Validation
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            response.put("message", errorMessage);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Check if product exists
            Optional<Product> existingProduct = productService.findById(id);
            if (existingProduct.isEmpty()) {
                response.put("message", "Sản phẩm không tồn tại");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Update product using service
            Product updatedProduct = productService.updateProduct(id, productDTO);
            response.put("message", "Cập nhật sản phẩm thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("message", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search-products")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword, @RequestParam(defaultValue = "all") String type) {
        List<Product> products = productService.searchProducts(keyword, type);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(p.getProductId());
            dto.setProductName(p.getProductName());
            dto.setProductType(p.getProductType());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            // Thêm số lượng tồn kho
            dto.setQuantity(inventoryService.getProductQuantity(p.getProductId()));
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

    @PutMapping("/update-pharmacy-info/{id}")
    public ResponseEntity<?> updatePharmacyInfo(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        Optional<Pharmacy> optionalPharmacy = pharmacyService.findById(id);
        if (optionalPharmacy.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nhà thuốc không tồn tại"));
        }
        Pharmacy pharmacy = optionalPharmacy.get();

        // Chỉ cho phép cập nhật địa chỉ, số điện thoại, email (không cho sửa tên)
        String address = (String) updates.get("address");
        String phone = (String) updates.get("phone");
        String email = (String) updates.get("email");

        // Validate trùng số điện thoại, email
        if (phone != null && !pharmacy.getPhone().equals(phone) && pharmacyService.existsByPhone(phone)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã tồn tại"));
        }
        if (email != null && !email.isBlank() && !email.equals(pharmacy.getEmail()) && pharmacyService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email đã tồn tại"));
        }

        // Cập nhật thông tin (chỉ địa chỉ, số điện thoại, email)
        if (address != null) pharmacy.setAddress(address);
        if (phone != null) pharmacy.setPhone(phone);
        if (email != null) pharmacy.setEmail(email);

        pharmacyRepository.save(pharmacy);
        return ResponseEntity.ok(Map.of("message", "Cập nhật thông tin nhà thuốc thành công"));
    }

    @GetMapping("/pharmacy/{id}")
    public ResponseEntity<Pharmacy> getPharmacy(@PathVariable Integer id) {
        Optional<Pharmacy> pharmacy = pharmacyService.findById(id);
        return pharmacy.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/update-product-quantity")
    public ResponseEntity<Map<String, String>> updateProductQuantity(
            @Valid @RequestBody InventoryDTO inventoryDTO,
            BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        // Check validation errors from Bean Validation
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            response.put("message", errorMessage);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Kiểm tra sản phẩm có tồn tại không
            if (!productService.existsById(inventoryDTO.getProductId())) {
                response.put("message", "Sản phẩm không tồn tại");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Cập nhật số lượng
            inventoryService.updateProductQuantity(inventoryDTO);

            response.put("message", "Cập nhật số lượng thành công");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("message", "Lỗi khi cập nhật số lượng: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search-pharmacies")
    public ResponseEntity<List<Map<String, Object>>> searchPharmacies(@RequestParam String keyword, @RequestParam(defaultValue = "all") String type) {
        List<Pharmacy> pharmacies = pharmacyService.searchPharmacies(keyword, type);
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

        return ResponseEntity.ok(result);
    }

    // Thêm các endpoint mới cho phân trang
    @GetMapping("/list-products-paginated")
    public ResponseEntity<Map<String, Object>> listProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();

        // Tính offset
        int offset = page * size;

        // Lấy tổng số sản phẩm
        long totalProducts = productService.getTotalProductCount();

        // Lấy sản phẩm theo phân trang
        List<Product> products = productService.getProductsPaginated(offset, size);

        // Convert to DTO
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(p.getProductId());
            dto.setProductName(p.getProductName());
            dto.setProductType(p.getProductType());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            // Thêm số lượng tồn kho
            dto.setQuantity(inventoryService.getProductQuantity(p.getProductId()));
            dtos.add(dto);
        }

        response.put("products", dtos);
        response.put("totalItems", totalProducts);
        response.put("totalPages", (int) Math.ceil((double) totalProducts / size));
        response.put("currentPage", page);
        response.put("pageSize", size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list-pharmacies-paginated")
    public ResponseEntity<Map<String, Object>> listPharmaciesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();

        // Tính offset
        int offset = page * size;

        // Lấy tổng số nhà thuốc
        long totalPharmacies = pharmacyService.getTotalPharmacyCount();

        // Lấy nhà thuốc theo phân trang
        List<Pharmacy> pharmacies = pharmacyService.getPharmaciesPaginated(offset, size);

        // Convert to Map để tránh Hibernate proxy
        List<Map<String, Object>> pharmacyMaps = new ArrayList<>();
        for (Pharmacy p : pharmacies) {
            Map<String, Object> map = new HashMap<>();
            map.put("pharmacyId", p.getPharmacyId());
            map.put("pharmacyName", p.getPharmacyName());
            map.put("address", p.getAddress());
            map.put("phone", p.getPhone());
            map.put("email", p.getEmail());
            map.put("isActive", p.getIsActive());

            // Lấy manager
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
            pharmacyMaps.add(map);
        }

        response.put("pharmacies", pharmacyMaps);
        response.put("totalItems", totalPharmacies);
        response.put("totalPages", (int) Math.ceil((double) totalPharmacies / size));
        response.put("currentPage", page);
        response.put("pageSize", size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-products-paginated")
    public ResponseEntity<Map<String, Object>> searchProductsPaginated(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        int offset = page * size;


        long totalProducts = productService.getSearchProductCount(keyword, type);
        List<Product> products = productService.searchProductsPaginated(keyword, type, offset, size);

        // Convert to DTO
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductDTO dto = new ProductDTO();
            dto.setProductId(p.getProductId());
            dto.setProductName(p.getProductName());
            dto.setProductType(p.getProductType());
            dto.setUnit(p.getUnit());
            dto.setPrice(p.getPrice());
            dto.setDescription(p.getDescription());
            // Thêm số lượng tồn kho
            dto.setQuantity(inventoryService.getProductQuantity(p.getProductId()));
            dtos.add(dto);
        }

        response.put("products", dtos);
        response.put("totalItems", totalProducts);
        response.put("totalPages", (int) Math.ceil((double) totalProducts / size));
        response.put("currentPage", page);
        response.put("pageSize", size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-pharmacies-paginated")
    public ResponseEntity<Map<String, Object>> searchPharmaciesPaginated(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        int offset = page * size;

        // Lấy kết quả tìm kiếm với phân trang
        long totalPharmacies = pharmacyService.getSearchPharmacyCount(keyword, type);
        List<Pharmacy> pharmacies = pharmacyService.searchPharmaciesPaginated(keyword, type, offset, size);

        // Convert to Map
        List<Map<String, Object>> result = new ArrayList<>();
        for (Pharmacy p : pharmacies) {
            Map<String, Object> map = new HashMap<>();
            map.put("pharmacyId", p.getPharmacyId());
            map.put("pharmacyName", p.getPharmacyName());
            map.put("address", p.getAddress());
            map.put("phone", p.getPhone());
            map.put("email", p.getEmail());
            map.put("isActive", p.getIsActive());

            // Lấy manager
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

        response.put("pharmacies", result);
        response.put("totalItems", totalPharmacies);
        response.put("totalPages", (int) Math.ceil((double) totalPharmacies / size));
        response.put("currentPage", page);
        response.put("pageSize", size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getAdminProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập");
            return ResponseEntity.status(403).body(errorResponse);
        }
        User admin = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (admin == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }
        UserInformation userInfo = userInformationService.findUserInformationByUserId(admin.getUserId());
        Map<String, Object> profile = new HashMap<>();
        profile.put("role", admin.getRole());
        if (userInfo != null) {
            profile.put("fullName", userInfo.getFullName() != null ? userInfo.getFullName() : admin.getUsername());
        } else {
            profile.put("fullName", admin.getUsername());
        }
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/showprofile")
    public ResponseEntity<?> showAdminProfile(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Phiên đăng nhập đã hết hạn");
            return ResponseEntity.status(401).body(errorResponse);
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized access");
            errorResponse.put("message", "Bạn không có quyền truy cập");
            return ResponseEntity.status(403).body(errorResponse);
        }
        User admin = userRepository.findById(currentUser.getUserId()).orElse(null);
        if (admin == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            errorResponse.put("message", "Không tìm thấy thông tin người dùng");
            return ResponseEntity.status(404).body(errorResponse);
        }
        UserInformation userInfo = userInformationService.findUserInformationByUserId(admin.getUserId());
        Map<String, Object> profile = new HashMap<>();
        profile.put("role", admin.getRole());
        if (userInfo != null) {
            profile.put("fullName", userInfo.getFullName() != null ? userInfo.getFullName() : admin.getUsername());
            profile.put("phone", userInfo.getPhone() != null ? userInfo.getPhone() : "Chưa cập nhật");
            profile.put("email", userInfo.getEmail() != null ? userInfo.getEmail() : "Chưa cập nhật");
        } else {
            profile.put("fullName", admin.getUsername());
            profile.put("phone", "Chưa cập nhật");
            profile.put("email", "Chưa cập nhật");
        }
        return ResponseEntity.ok(profile);
    }
}