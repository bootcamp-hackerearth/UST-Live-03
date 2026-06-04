package com.ust.pos.customer;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    public static final String CUSTOMER = "customer";
    public static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";
    public static final String CUSTOMERS = "customers";

    @Autowired
    CustomerService customerService;

    @Autowired
    AddressService addressService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute(CUSTOMERS, customerService.findAll(pageable));
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CustomerDto customerDto) {
        model.addAttribute(CUSTOMERS, customerDto);
        model.addAttribute("addresses", addressService.findAll());
        return "customer/add";
    }

    @PostMapping("/add")
    public String addcustomer(Model model, @ModelAttribute CustomerDto customerDto) {

        CustomerDto response = customerService.save(customerDto);

        if (!response.isSuccess()) {
            model.addAttribute("customerDto", response);
            model.addAttribute("message", response.getMessage());
            return "customer/add";
        }

        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        customerService.delete(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        CustomerDto customerDto = customerService.findByIdentifier(identifier);
        List<AddressDto> existingAddresses = addressService.findAllByPhoneNumber(identifier);
        AddressDto existingBilling = null;
        AddressDto existingShipping = null;
        for (AddressDto addr : existingAddresses) {
            if ("billing".equalsIgnoreCase(addr.getAddressType())) {
                existingBilling = addr;
            } else if ("shipping".equalsIgnoreCase(addr.getAddressType())) {
                existingShipping = addr;
            }
        }
        customerDto.setBillingAddress(existingBilling);
        customerDto.setShippingAddress(existingShipping);
        model.addAttribute(CUSTOMER, customerDto);

        return "customer/customer";
    }

    @PostMapping("/update")
    public String updatePrice(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.update(customerDto);
        model.addAttribute(CUSTOMER, response);
        if (!response.isSuccess()) {
            model.addAttribute(CUSTOMER, response);
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/togglestatus")
    public String toggleCustomerStatus(@RequestParam String identifier) {
        customerService.updateStatus(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }
}
