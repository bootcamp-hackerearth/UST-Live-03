package com.ust.pos.customer;

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

    public static final String CUSTOMER_DTO = "customerDto";
    private static final String CUSTOMER_LIST = "customer/list";
    private static final String CUSTOMER_ADD = "customer/add";
    private static final String CUSTOMER_VIEW = "customer/customer";
    private static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";
    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("customers", customerService.findAll(pageable));
        return CUSTOMER_LIST;
    }

    @GetMapping("/add")
    public String add(Model model) {
        if (!model.containsAttribute(CUSTOMER_DTO)) {
            model.addAttribute(CUSTOMER_DTO, new CustomerDto());
        }
        return CUSTOMER_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute CustomerDto customerDto, Model model) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute(CUSTOMER_DTO, response);
            return CUSTOMER_ADD;
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String get(@RequestParam String identifier, Model model) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        if (response == null) {
            return REDIRECT_CUSTOMER_LIST;
        }
        model.addAttribute(CUSTOMER_DTO, response);
        return CUSTOMER_VIEW;
    }

    @PostMapping("/update")
    public String update(@ModelAttribute CustomerDto customerDto, Model model) {
        CustomerDto response = customerService.update(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute(CUSTOMER_DTO, response);
            return CUSTOMER_VIEW;
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        customerService.delete(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/toggle")
    public String toggle(@RequestParam String identifier) {
        customerService.toggleStatus(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }
}