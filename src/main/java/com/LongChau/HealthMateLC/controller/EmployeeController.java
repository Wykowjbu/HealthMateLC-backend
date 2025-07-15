package com.LongChau.HealthMateLC.controller;

import com.LongChau.HealthMateLC.model.Customer;
import com.LongChau.HealthMateLC.model.Product;
import com.LongChau.HealthMateLC.model.Schedule;
import com.LongChau.HealthMateLC.model.Invoice;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/danh-sach-khach-hang")
    public ResponseEntity<List<Customer>> getAllCustomers(){
        List<Customer> customers=customerService.getAll();
        return ResponseEntity.ok(customers);
    }

    @PostMapping("/tao-moi-khach-hang")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer){
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
        if (!isPhoneOrEmailExists(customer)) {
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

    @GetMapping ("/lich-lam-viec")
    public  ResponseEntity<List<Schedule>> getScheduleByUserId(@RequestParam Integer userId) {
        List<Schedule> schedules = scheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/lich-su-don-hang-khach-hang/{customerId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getOrderHistoryByCustomerId(@PathVariable Integer customerId) {
        List<InvoiceResponseDTO> orderHistory = invoiceService.getOrderHistoryDTOByCustomerId(customerId);
        return ResponseEntity.ok(orderHistory);
    }

    @GetMapping("/dach-sach-don-hang-hom-nay")
    public ResponseEntity<List<Invoice>> getTodayInvoices() {
        List<Invoice> todayInvoices = invoiceService.getAll(); // Assuming you want all invoices for today
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
        System.out.println(orderRequest.getStatus()+ " thong tin o day ---------------------------------------------");

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
