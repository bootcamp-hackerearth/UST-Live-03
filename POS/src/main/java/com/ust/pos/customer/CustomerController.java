package com.ust.pos.customer;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    public static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public String home(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute("customerDto") CustomerDto customerDto, @ModelAttribute("billing") AddressDto billing, @ModelAttribute("shipping") AddressDto shipping) {
        return "customer/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "customer/add";
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        CustomerDto customerDto = customerService.findByIdentifier(identifier);
        model.addAttribute("customerDto", customerDto);
        return "customer/customer";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto customerDto1 = customerService.update(customerDto);
        if (!customerDto1.isSuccess()) {
            model.addAttribute("message", customerDto1.getMessage());
            model.addAttribute("customerDto", customerDto);
            return "customer/customer";
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier) {
        customerService.deleteByIdentifier(identifier);
        addressService.delete(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }
}