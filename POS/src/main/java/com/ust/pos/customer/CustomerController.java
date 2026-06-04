package com.ust.pos.customer;

import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.shelf.service.ShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    public static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";

    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("customers", customerService.findAll(pageable));
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CustomerDto customerDto) {
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
    public String update(Model model, @RequestParam String phoneNo) {
        CustomerDto response = customerService.findByIdentifierWithAddressDto(phoneNo);
        model.addAttribute("customerDto", response);
        return "customer/customer";
    }

    @PostMapping("/update")
    public String updatePost(Model model, @ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.update(customerDto);

        if (!response.isSuccess()) {
            model.addAttribute("customerDto", response);
            model.addAttribute("message", response.getMessage());
            return "customer/customer";
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(Model model, @RequestParam String phoneNo) {
        customerService.delete(phoneNo);
        return REDIRECT_CUSTOMER_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(Model model, @RequestParam String identifier) {
        customerService.toggleStatus(identifier);
    }
}