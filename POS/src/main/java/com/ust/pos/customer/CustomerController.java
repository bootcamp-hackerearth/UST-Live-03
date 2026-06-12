package com.ust.pos.customer;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/customer/list";
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("customer", customerService.findAll(pageable));
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model, Pageable pageable) {
        model.addAttribute("customers", customerService.findAll(pageable));
        model.addAttribute("customerDto", new CustomerDto());
        return "customer/add";
    }

    @PostMapping("/add")
    public String addPost(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "customer/add";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/get")
    public String update(Model model, @RequestParam String identifier) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        response.setBillingAddress(addressService.findByPhoneNoAndAddressType(response.getPhoneNo(), "billingAddress"));
        response.setShippingAddress(addressService.findByPhoneNoAndAddressType(response.getPhoneNo(), "shippingAddress"));
        model.addAttribute("customerDto", response);
        return "customer/customer";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.update(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return "customer/customer";
        }
        return REDIRECT_ROLE_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String identifier, Long phoneNo) {
        customerService.delete(identifier, phoneNo);
        return REDIRECT_ROLE_LIST;
    }
}


