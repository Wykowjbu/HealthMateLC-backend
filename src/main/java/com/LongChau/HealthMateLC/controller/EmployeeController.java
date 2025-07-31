package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.*;
import com.LongChau.HealthMateLC.model.Inventory;
import com.LongChau.HealthMateLC.dto.EmployeeInfoDTO;
import com.LongChau.HealthMateLC.dto.UserHistoryDTO;
import com.LongChau.HealthMateLC.dto.CreateOrderRequestDTO;
import com.LongChau.HealthMateLC.dto.InvoiceResponseDTO;
import com.LongChau.HealthMateLC.service.CustomerService;
import com.LongChau.HealthMateLC.service.ProductsService;
import com.LongChau.HealthMateLC.service.ScheduleService;
import com.LongChau.HealthMateLC.service.InvoiceService;
import com.LongChau.HealthMateLC.service.UserInformationService;
import com.LongChau.HealthMateLC.service.UserHistoryService;
import com.LongChau.HealthMateLC.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductsService productsService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private UserInformationService userInformationService;
    @Autowired
    private UserHistoryService userHistoryService;
    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/danh-sach-khach-hang")
    public ResponseEntity<List<Customer>> getAllCustomers(){
        List<Customer> customers=customerService.getAll();
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/tao-moi-khach-hang")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
        System.out.println("Creating new customer: " + customer);
        if (isPhoneOrEmailExists(customer)) {
            return ResponseEntity.badRequest().body(customer);
        }
        Customer addNewCustomer=customerService.addNewCustomer(customer);
        return ResponseEntity.ok(addNewCustomer);
    }


    @PutMapping("/cap-nhat-khach-hang/{id}")
    public ResponseEntity<Customer> updateCustomerById(@PathVariable Integer id, @RequestBody Customer customer) {
        // Set the customer ID from path variable
        customer.setCustomerId(id);
        if (isPhoneOrEmailExists(customer)) {  // Sửa logic: nếu phone/email ĐÃ tồn tại thì báo lỗi
            return ResponseEntity.badRequest().body(customer);
        }
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    public boolean isPhoneOrEmailExists(Customer customer) {
        List<Customer> customers = customerService.getAll();
        for (Customer existingCustomer : customers) {
            if (!existingCustomer.getCustomerId().equals(customer.getCustomerId())) {
                if (existingCustomer.getPhone() != null && existingCustomer.getPhone().equals(customer.getPhone())) {
                    return true;
                }
                if (existingCustomer.getEmail() != null && existingCustomer.getEmail().equals(customer.getEmail())) {
                    return true;
                }
            }
        }
        return false;
    }

    @GetMapping("/danh-sach-san-pham")
    public ResponseEntity<List<Product>> getAllProducts(){
        List<Product> list= productsService.getAll();
        return ResponseEntity.ok(list);
        //phanhuy
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAll();

        return ResponseEntity.ok(inventoryList);
    }

    @GetMapping ("/lich-lam-viec")
    public  ResponseEntity<List<Schedule>> getScheduleByUserId(@RequestParam Integer userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/lich-su-don-hang")
    public ResponseEntity<List<Invoice>> getOrderHistoryByCustomerId(@RequestParam Integer customerId) {
        List<Invoice> orderHistory = invoiceService.getOrderHistoryByCustomerId(customerId);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("/danh-sach-don-hang")
    public ResponseEntity<List<InvoiceResponseDTO>> getTodayInvoices() {
        List<InvoiceResponseDTO> todayInvoices = invoiceService.getAllInvoicesWithDetails();
        System.out.println(todayInvoices);
        return ResponseEntity.ok(todayInvoices);
    }

    @GetMapping("/thong-tin-nhan-vien/{id}")
    public ResponseEntity<EmployeeInfoDTO> getEmployeeInfo(@PathVariable Integer id) {
        EmployeeInfoDTO employeeInfo = userInformationService.getEmployeeInfoById(id);

        if (employeeInfo != null) {
            return ResponseEntity.ok(employeeInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/lich-su-cong-tac/{userId}")
    public ResponseEntity<List<UserHistoryDTO>> getUserWorkHistory(@PathVariable Integer userId) {
        List<UserHistoryDTO> workHistory = userHistoryService.getUserWorkHistoryByUserId(userId);
        return ResponseEntity.ok(workHistory);
    }

    @PostMapping("/tao-don-hang")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequestDTO orderRequest) {

        try {
            InvoiceResponseDTO createdInvoice = invoiceService.createOrder(orderRequest);
            return ResponseEntity.ok(createdInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating order123: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error occurred");
        }
    }

}
