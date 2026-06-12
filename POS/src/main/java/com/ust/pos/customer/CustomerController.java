package com.ust.pos.customer;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/customer")
public class CustomerController {
    public static final String CUSTOMER_DTO = "customerDto";
    @Autowired
    CustomerService customerService;
    @Autowired
    AddressService addressService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        WsDto<CustomerDto> customers = customerService.findAll(pageable);
        model.addAttribute("customers", customers);
        return "customer/list";
    }

    @GetMapping("/add")
    public String getsave(Model model) {
        model.addAttribute(CUSTOMER_DTO, new CustomerDto());
        return "customer/add";
    }

    @PostMapping("/add")
    public String save(Model model, @ModelAttribute(CUSTOMER_DTO) CustomerDto customerDto) {
        CustomerDto response = customerService.save(customerDto);
        //  show message on same page
        model.addAttribute("success", response.isStatus());
        model.addAttribute("message", response.getMessage());
        //  keep user on add page
        model.addAttribute(CUSTOMER_DTO, new CustomerDto());
        return "customer/add";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        customerService.deleteByIdentifier(identifier);
        return "redirect:/customer/list";
    }

    @GetMapping("/update")
    public String update(Model model, @RequestParam String identifier) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        model.addAttribute(CUSTOMER_DTO, response);
        return "customer/customer";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.update(customerDto);
        model.addAttribute(CUSTOMER_DTO, response);
        return "customer/customer";
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier, boolean status) {
        customerService.changeCustomerStatus(identifier, status);
        return "redirect:/customer/list";
    }
}