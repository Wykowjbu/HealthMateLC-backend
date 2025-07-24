package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.model.Schedule;
import com.LongChau.HealthMateLC.model.Invoice;
import com.LongChau.HealthMateLC.dto.CreateOrderRequestDTO;
import com.LongChau.HealthMateLC.dto.InvoiceResponseDTO;
import com.LongChau.HealthMateLC.dto.EmployeeInfoDTO;
import com.LongChau.HealthMateLC.dto.UserHistoryDTO;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.InvoiceService;
import com.LongChau.HealthMateLC.service.ProductsService;
import com.LongChau.HealthMateLC.service.ScheduleService;
import com.LongChau.HealthMateLC.service.UserHistoryService;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private ProductsService productsService;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private UserInformationService userInformationService;

    @Mock
    private UserHistoryService userHistoryService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Nguyen Van A");
        newCustomer.setPhone("0123456789");
        newCustomer.setEmail("nguyenvana@gmail.com");
        newCustomer.setGender("Nam");
        newCustomer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        newCustomer.setMedicalHistory("Không có tiền sử bệnh");
        newCustomer.setAllergies("Không có dị ứng");

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(1);
        savedCustomer.setFullName("Nguyen Van A");
        savedCustomer.setPhone("0123456789");
        savedCustomer.setEmail("nguyenvana@gmail.com");
        savedCustomer.setGender("Nam");
        savedCustomer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        savedCustomer.setMedicalHistory("Không có tiền sử bệnh");
        savedCustomer.setAllergies("Không có dị ứng");
        savedCustomer.setTotalPoints(0);
        savedCustomer.setCreatedDate(LocalDateTime.now());

        // Mock service calls
        when(customerService.getAll()).thenReturn(Arrays.asList());
        when(customerService.addNewCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // Act & Assert
        mockMvc.perform(post("/employee/tao-moi-khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.phone").value("0123456789"))
                .andExpect(jsonPath("$.email").value("nguyenvana@gmail.com"))
                .andExpect(jsonPath("$.gender").value("Nam"))
                .andExpect(jsonPath("$.totalPoints").value(0));

        verify(customerService, times(1)).getAll();
        verify(customerService, times(1)).addNewCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_DuplicatePhone_ReturnsBadRequest() throws Exception {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(1);
        existingCustomer.setFullName("Nguyen Van B");
        existingCustomer.setPhone("0123456789");
        existingCustomer.setEmail("nguyenvanb@gmail.com");

        Customer newCustomer = new Customer();
        newCustomer.setFullName("Nguyen Van A");
        newCustomer.setPhone("0123456789"); // Same phone as existing customer
        newCustomer.setEmail("nguyenvana@gmail.com");
        newCustomer.setGender("Nam");
        newCustomer.setDateOfBirth(LocalDate.of(1990, 1, 1));

        // Mock service call to return existing customer with same phone
        when(customerService.getAll()).thenReturn(Arrays.asList(existingCustomer));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-moi-khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phone").value("0123456789"));

        verify(customerService, times(1)).getAll();
        verify(customerService, never()).addNewCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_DuplicateEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(1);
        existingCustomer.setFullName("Nguyen Van B");
        existingCustomer.setPhone("0987654321");
        existingCustomer.setEmail("nguyenvana@gmail.com");

        Customer newCustomer = new Customer();
        newCustomer.setFullName("Nguyen Van A");
        newCustomer.setPhone("0123456789");
        newCustomer.setEmail("nguyenvana@gmail.com"); // Same email as existing customer
        newCustomer.setGender("Nam");
        newCustomer.setDateOfBirth(LocalDate.of(1990, 1, 1));

        // Mock service call to return existing customer with same email
        when(customerService.getAll()).thenReturn(Arrays.asList(existingCustomer));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-moi-khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("nguyenvana@gmail.com"));

        verify(customerService, times(1)).getAll();
        verify(customerService, never()).addNewCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_WithMinimalRequiredFields_Success() throws Exception {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Tran Thi C");
        // Only required field provided

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(2);
        savedCustomer.setFullName("Tran Thi C");
        savedCustomer.setTotalPoints(0);
        savedCustomer.setCreatedDate(LocalDateTime.now());

        // Mock service calls
        when(customerService.getAll()).thenReturn(Arrays.asList());
        when(customerService.addNewCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // Act & Assert
        mockMvc.perform(post("/employee/tao-moi-khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(2))
                .andExpect(jsonPath("$.fullName").value("Tran Thi C"))
                .andExpect(jsonPath("$.totalPoints").value(0));

        verify(customerService, times(1)).getAll();
        verify(customerService, times(1)).addNewCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_WithCompleteInformation_Success() throws Exception {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Le Van D");
        newCustomer.setPhone("0369852147");
        newCustomer.setEmail("levand@gmail.com");
        newCustomer.setGender("Nam");
        newCustomer.setDateOfBirth(LocalDate.of(1985, 5, 15));
        newCustomer.setMedicalHistory("Tiểu đường type 2");
        newCustomer.setAllergies("Dị ứng penicillin");

        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(3);
        savedCustomer.setFullName("Le Van D");
        savedCustomer.setPhone("0369852147");
        savedCustomer.setEmail("levand@gmail.com");
        savedCustomer.setGender("Nam");
        savedCustomer.setDateOfBirth(LocalDate.of(1985, 5, 15));
        savedCustomer.setMedicalHistory("Tiểu đường type 2");
        savedCustomer.setAllergies("Dị ứng penicillin");
        savedCustomer.setTotalPoints(0);
        savedCustomer.setCreatedDate(LocalDateTime.now());

        // Mock service calls
        when(customerService.getAll()).thenReturn(Arrays.asList());
        when(customerService.addNewCustomer(any(Customer.class))).thenReturn(savedCustomer);

        // Act & Assert
        mockMvc.perform(post("/employee/tao-moi-khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(3))
                .andExpect(jsonPath("$.fullName").value("Le Van D"))
                .andExpect(jsonPath("$.phone").value("0369852147"))
                .andExpect(jsonPath("$.email").value("levand@gmail.com"))
                .andExpect(jsonPath("$.gender").value("Nam"))
                .andExpect(jsonPath("$.medicalHistory").value("Tiểu đường type 2"))
                .andExpect(jsonPath("$.allergies").value("Dị ứng penicillin"))
                .andExpect(jsonPath("$.totalPoints").value(0));

        verify(customerService, times(1)).getAll();
        verify(customerService, times(1)).addNewCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomer_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Pham Thi E");
        newCustomer.setPhone("0147258369");
        newCustomer.setEmail("phamthie@gmail.com");

        // Mock service calls
        when(customerService.getAll()).thenReturn(Arrays.asList());
        when(customerService.addNewCustomer(any(Customer.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-moi-khach-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).getAll();
        verify(customerService, times(1)).addNewCustomer(any(Customer.class));
    }

    // ===== TEST CASES CHO TẠO ĐỚN HÀNG MỚI =====

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = createValidOrderRequest();
        InvoiceResponseDTO expectedResponse = createMockInvoiceResponse();

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(250000))
                .andExpect(jsonPath("$.status").value("Đã thanh toán"))
                .andExpect(jsonPath("$.invoiceDetails").isArray())
                .andExpect(jsonPath("$.invoiceDetails", hasSize(2)));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    @Test
    void testCreateOrder_WithSingleItem_Success() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setEmployeeId(1);
        orderRequest.setCustomerId(1);
        orderRequest.setTotalAmount(new BigDecimal("100000"));
        orderRequest.setPaymentMethod("Tiền mặt");
        orderRequest.setStatus("Đã thanh toán");
        orderRequest.setInvoiceDate("2025-01-24");

        CreateOrderRequestDTO.OrderItemDTO orderItem = new CreateOrderRequestDTO.OrderItemDTO();
        orderItem.setProductId(1);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal("100000"));
        orderRequest.setOrderItems(Arrays.asList(orderItem));

        InvoiceResponseDTO expectedResponse = new InvoiceResponseDTO();
        expectedResponse.setInvoiceId(2);
        expectedResponse.setCustomerId(1);
        expectedResponse.setEmployeeId(1);
        expectedResponse.setTotalAmount(new BigDecimal("100000"));
        expectedResponse.setStatus("Đã thanh toán");

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(2))
                .andExpect(jsonPath("$.totalAmount").value(100000));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    @Test
    void testCreateOrder_RuntimeException_ReturnsBadRequest() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = createValidOrderRequest();

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenThrow(new RuntimeException("Không đủ số lượng sản phẩm trong kho"));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error creating order123: Không đủ số lượng sản phẩm trong kho")));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    @Test
    void testCreateOrder_GeneralException_ReturnsInternalServerError() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = createValidOrderRequest();

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenThrow(new Exception("Database connection failed"));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error occurred"));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    @Test
    void testCreateOrder_InvalidCustomerId_ReturnsBadRequest() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setCustomerId(999); // ID không tồn tại

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenThrow(new RuntimeException("Khách hàng không tồn tại"));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Khách hàng không tồn tại")));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    @Test
    void testCreateOrder_EmptyOrderItems_ReturnsBadRequest() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setEmployeeId(1);
        orderRequest.setCustomerId(1);
        orderRequest.setTotalAmount(new BigDecimal("0"));
        orderRequest.setPaymentMethod("Tiền mặt");
        orderRequest.setStatus("Đã thanh toán");
        orderRequest.setOrderItems(Arrays.asList()); // Danh sách rỗng

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenThrow(new RuntimeException("Đơn hàng phải có ít nhất một sản phẩm"));

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Đơn hàng phải có ít nhất một sản phẩm")));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    @Test
    void testCreateOrder_WithCardPayment_Success() throws Exception {
        // Arrange
        CreateOrderRequestDTO orderRequest = createValidOrderRequest();
        orderRequest.setPaymentMethod("Thẻ tín dụng");

        InvoiceResponseDTO expectedResponse = createMockInvoiceResponse();
        expectedResponse.setPayment("Thẻ tín dụng");

        when(invoiceService.createOrder(any(CreateOrderRequestDTO.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/employee/tao-don-hang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payment").value("Thẻ tín dụng"));

        verify(invoiceService, times(1)).createOrder(any(CreateOrderRequestDTO.class));
    }

    // ===== TEST CASES CHO DANH SÁCH KHÁCH HÀNG =====

    @Test
    void testGetAllCustomers_Success() throws Exception {
        // Arrange
        List<Customer> mockCustomers = Arrays.asList(
                createMockCustomer(1, "Nguyen Van A", "0123456789", "nguyenvana@gmail.com"),
                createMockCustomer(2, "Tran Thi B", "0987654321", "tranthib@gmail.com")
        );

        when(customerService.getAll()).thenReturn(mockCustomers);

        // Act & Assert
        mockMvc.perform(get("/employee/danh-sach-khach-hang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].fullName").value("Nguyen Van A"))
                .andExpect(jsonPath("$[1].customerId").value(2))
                .andExpect(jsonPath("$[1].fullName").value("Tran Thi B"));

        verify(customerService, times(1)).getAll();
    }

    @Test
    void testGetAllCustomers_EmptyList() throws Exception {
        // Arrange
        when(customerService.getAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/employee/danh-sach-khach-hang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(customerService, times(1)).getAll();
    }

    // ===== TEST CASES CHO CẬP NHẬT KHÁCH HÀNG =====

    @Test
    void testUpdateCustomer_Success() throws Exception {
        // Arrange
        Customer updateCustomer = createMockCustomer(1, "Nguyen Van A Updated", "0123456789", "updated@gmail.com");

        when(customerService.getAll()).thenReturn(Arrays.asList());
        when(customerService.updateCustomer(any(Customer.class))).thenReturn(updateCustomer);

        // Act & Assert
        mockMvc.perform(put("/employee/cap-nhat-khach-hang/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.fullName").value("Nguyen Van A Updated"))
                .andExpect(jsonPath("$.email").value("updated@gmail.com"));

        verify(customerService, times(1)).getAll();
        verify(customerService, times(1)).updateCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_DuplicatePhone_ReturnsBadRequest() throws Exception {
        // Arrange
        Customer existingCustomer = createMockCustomer(2, "Other Customer", "0123456789", "other@gmail.com");
        Customer updateCustomer = createMockCustomer(1, "Nguyen Van A", "0123456789", "nguyenvana@gmail.com");

        when(customerService.getAll()).thenReturn(Arrays.asList(existingCustomer));

        // Act & Assert
        mockMvc.perform(put("/employee/cap-nhat-khach-hang/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCustomer)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).getAll();
        verify(customerService, never()).updateCustomer(any(Customer.class));
    }

    // ===== TEST CASES CHO DANH SÁCH SẢN PHẨM =====

    @Test
    void testGetAllProducts_Success() throws Exception {
        // Arrange
        List<Product> mockProducts = Arrays.asList(
                createMockProduct(1, "Paracetamol 500mg", "Thuốc", new BigDecimal("50000")),
                createMockProduct(2, "Vitamin C 1000mg", "Thực phẩm chức năng", new BigDecimal("100000"))
        );

        when(productsService.getAll()).thenReturn(mockProducts);

        // Act & Assert
        mockMvc.perform(get("/employee/danh-sach-san-pham"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Paracetamol 500mg"))
                .andExpect(jsonPath("$[1].productId").value(2))
                .andExpect(jsonPath("$[1].productName").value("Vitamin C 1000mg"));

        verify(productsService, times(1)).getAll();
    }

    @Test
    void testGetAllProducts_EmptyList() throws Exception {
        // Arrange
        when(productsService.getAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/employee/danh-sach-san-pham"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productsService, times(1)).getAll();
    }

    // ===== TEST CASES CHO LỊCH LÀM VIỆC =====

    @Test
    void testGetScheduleByUserId_Success() throws Exception {
        // Arrange
        List<Schedule> mockSchedules = Arrays.asList(
                createMockSchedule(1, 1, "Ca sáng", "08:00", "12:00"),
                createMockSchedule(2, 1, "Ca chiều", "13:00", "17:00")
        );

        when(scheduleService.getSchedulesByUserId(1)).thenReturn(mockSchedules);

        // Act & Assert
        mockMvc.perform(get("/employee/lich-lam-viec")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(scheduleService, times(1)).getSchedulesByUserId(1);
    }

    @Test
    void testGetScheduleByUserId_EmptySchedule() throws Exception {
        // Arrange
        when(scheduleService.getSchedulesByUserId(1)).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/employee/lich-lam-viec")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(scheduleService, times(1)).getSchedulesByUserId(1);
    }

    // ===== TEST CASES CHO LỊCH SỬ ĐƠN HÀNG =====

    @Test
    void testGetOrderHistoryByCustomerId_Success() throws Exception {
        // Arrange
        List<Invoice> mockInvoices = Arrays.asList(
                createMockInvoice(1, 1, new BigDecimal("150000"), "Đã thanh toán"),
                createMockInvoice(2, 1, new BigDecimal("200000"), "Đã thanh toán")
        );

        when(invoiceService.getOrderHistoryByCustomerId(1)).thenReturn(mockInvoices);

        // Act & Assert
        mockMvc.perform(get("/employee/lich-su-don-hang")
                        .param("customerId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(invoiceService, times(1)).getOrderHistoryByCustomerId(1);
    }

    // ===== TEST CASES CHO ĐƠN HÀNG HÔM NAY =====

    @Test
    void testGetTodayInvoices_Success() throws Exception {
        // Arrange
        List<Invoice> todayInvoices = Arrays.asList(
                createMockInvoice(1, 1, new BigDecimal("150000"), "Đã thanh toán"),
                createMockInvoice(2, 2, new BigDecimal("300000"), "Đã thanh toán")
        );

        when(invoiceService.getAll()).thenReturn(todayInvoices);

        // Act & Assert
        mockMvc.perform(get("/employee/dach-sach-don-hang-hom-nay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(invoiceService, times(1)).getAll();
    }

    // ===== TEST CASES CHO THÔNG TIN NHÂN VIÊN =====

    @Test
    void testGetEmployeeInfo_Success() throws Exception {
        // Arrange
        EmployeeInfoDTO mockEmployeeInfo = createMockEmployeeInfo(1, "Tran Van C", "Employee", "employee@gmail.com");

        when(userInformationService.getEmployeeInfoById(1)).thenReturn(mockEmployeeInfo);

        // Act & Assert
        mockMvc.perform(get("/employee/thong-tin-nhan-vien/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.fullName").value("Tran Van C"))
                .andExpect(jsonPath("$.role").value("Employee"));

        verify(userInformationService, times(1)).getEmployeeInfoById(1);
    }

    @Test
    void testGetEmployeeInfo_NotFound() throws Exception {
        // Arrange
        when(userInformationService.getEmployeeInfoById(999)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/employee/thong-tin-nhan-vien/999"))
                .andExpect(status().isNotFound());

        verify(userInformationService, times(1)).getEmployeeInfoById(999);
    }

    // ===== TEST CASES CHO LỊCH SỬ CÔNG TÁC =====

    @Test
    void testGetUserWorkHistory_Success() throws Exception {
        // Arrange
        List<UserHistoryDTO> mockWorkHistory = Arrays.asList(
                createMockUserHistory(1, "Bắt đầu làm việc", "2024-01-01"),
                createMockUserHistory(2, "Thăng chức", "2024-06-01")
        );

        when(userHistoryService.getUserWorkHistoryByUserId(1)).thenReturn(mockWorkHistory);

        // Act & Assert
        mockMvc.perform(get("/employee/lich-su-cong-tac/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(userHistoryService, times(1)).getUserWorkHistoryByUserId(1);
    }

    // ===== HELPER METHODS =====

    private Customer createMockCustomer(Integer id, String fullName, String phone, String email) {
        Customer customer = new Customer();
        customer.setCustomerId(id);
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setTotalPoints(0);
        customer.setCreatedDate(LocalDateTime.now());
        return customer;
    }

    private Product createMockProduct(Integer id, String name, String type, BigDecimal price) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName(name);
        product.setProductType(type);
        product.setPrice(price);
        product.setUnit("Hộp");
        product.setDescription("Mô tả sản phẩm");
        return product;
    }

    private Schedule createMockSchedule(Integer id, Integer userId, String shift, String startTime, String endTime) {
        Schedule schedule = new Schedule();
        // Assuming Schedule has these fields - adjust based on actual model
        return schedule;
    }

    private Invoice createMockInvoice(Integer id, Integer customerId, BigDecimal totalAmount, String status) {
        Invoice invoice = new Invoice();
        // Assuming Invoice has these fields - adjust based on actual model
        return invoice;
    }

    private EmployeeInfoDTO createMockEmployeeInfo(Integer userId, String fullName, String role, String email) {
        EmployeeInfoDTO employeeInfo = new EmployeeInfoDTO();
        // Assuming EmployeeInfoDTO has these fields - adjust based on actual DTO
        return employeeInfo;
    }

    private UserHistoryDTO createMockUserHistory(Integer id, String action, String date) {
        UserHistoryDTO userHistory = new UserHistoryDTO();
        // Assuming UserHistoryDTO has these fields - adjust based on actual DTO
        return userHistory;
    }

    // Helper methods
    private CreateOrderRequestDTO createValidOrderRequest() {
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setEmployeeId(1);
        orderRequest.setCustomerId(1);
        orderRequest.setTotalAmount(new BigDecimal("250000"));
        orderRequest.setPaymentMethod("Tiền mặt");
        orderRequest.setStatus("Đã thanh toán");
        orderRequest.setInvoiceDate("2025-01-24");

        // Tạo 2 sản phẩm trong đơn hàng
        CreateOrderRequestDTO.OrderItemDTO item1 = new CreateOrderRequestDTO.OrderItemDTO();
        item1.setProductId(1);
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("100000"));

        CreateOrderRequestDTO.OrderItemDTO item2 = new CreateOrderRequestDTO.OrderItemDTO();
        item2.setProductId(2);
        item2.setQuantity(1);
        item2.setUnitPrice(new BigDecimal("50000"));

        orderRequest.setOrderItems(Arrays.asList(item1, item2));
        return orderRequest;
    }

    private InvoiceResponseDTO createMockInvoiceResponse() {
        InvoiceResponseDTO response = new InvoiceResponseDTO();
        response.setInvoiceId(1);
        response.setPharmacyId(1);
        response.setPharmacyName("Nhà thuốc Long Châu");
        response.setCustomerId(1);
        response.setCustomerName("Nguyen Van A");
        response.setEmployeeId(1);
        response.setEmployeeName("Tran Thi B");
        response.setInvoiceDate(LocalDateTime.now());
        response.setTotalAmount(new BigDecimal("250000"));
        response.setPointsEarned(25);
        response.setPayment("Tiền mặt");
        response.setStatus("Đã thanh toán");

        // Tạo chi tiết hóa đơn
        InvoiceResponseDTO.InvoiceDetailResponseDTO detail1 = new InvoiceResponseDTO.InvoiceDetailResponseDTO();
        detail1.setInvoiceDetailId(1);
        detail1.setProductId(1);
        detail1.setProductName("Paracetamol 500mg");
        detail1.setQuantity(2);
        detail1.setPrice(new BigDecimal("100000"));

        InvoiceResponseDTO.InvoiceDetailResponseDTO detail2 = new InvoiceResponseDTO.InvoiceDetailResponseDTO();
        detail2.setInvoiceDetailId(2);
        detail2.setProductId(2);
        detail2.setProductName("Vitamin C 1000mg");
        detail2.setQuantity(1);
        detail2.setPrice(new BigDecimal("50000"));

        response.setInvoiceDetails(Arrays.asList(detail1, detail2));
        return response;
    }
}
