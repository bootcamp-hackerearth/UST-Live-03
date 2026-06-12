package com.ust.pos.customer;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    public static final String CUSTOMER_LIST = "customer/list";
    public static final String CUSTOMERS = "customers";
    public static final String MESSAGE = "message";
    @Autowired
    CustomerService customerService;

    @Autowired
    AddressService addressService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(CUSTOMERS, customerService.findAll(pageable));
        return CUSTOMER_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CustomerDto customerDto) {
        return "customer/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CustomerDto customerDto, Pageable pageable) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Customer Added Successfully");
        }
        model.addAttribute(CUSTOMERS, customerService.findAll(pageable));
        return CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        model.addAttribute(addressService.findByPhoneNoAndAddressType(response.getPhoneNo(), "BILLING"));
        model.addAttribute(addressService.findByPhoneNoAndAddressType(response.getPhoneNo(), "SHIPPING"));
        model.addAttribute("customer", response);
        return "customer/customer";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CustomerDto customerDto, Pageable pageable) {
        CustomerDto response = customerService.update(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute(MESSAGE, response.getMessage());
        } else {
            model.addAttribute(MESSAGE, "Updated Successfully");
        }
        model.addAttribute(CUSTOMERS, customerService.findAll(pageable));
        return CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Pageable pageable) {
        customerService.delete(identifier);
        model.addAttribute(CUSTOMERS, customerService.findAll(pageable));
        model.addAttribute(MESSAGE, "Customer deleted successfully");
        return CUSTOMER_LIST;
    }
}
