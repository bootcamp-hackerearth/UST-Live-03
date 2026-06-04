package com.ust.pos.customer;

import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    public static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String SUCCESS_MESSAGE = "successMessage";

    @Autowired
    private CustomerService customerService;

    @GetMapping("/list")
    public String home(Model model, Pageable pageable) {
        model.addAttribute("customers", customerService.findAll(pageable));
        return "customer/list";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("customerDto", new CustomerDto());
        return "customer/add";
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute CustomerDto customerDto,
                          RedirectAttributes redirectAttributes) {

        CustomerDto response = customerService.save(customerDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/customer/add";
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Customer added successfully!");
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String update(Model model,
                         @RequestParam String phoneNo,
                         RedirectAttributes redirectAttributes) {

        try {
            CustomerDto response = customerService.findByIdentifierWithAddressDto(phoneNo);
            model.addAttribute("customerDto", response);
            return "customer/customer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Customer not found!");
            return REDIRECT_CUSTOMER_LIST;
        }
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute CustomerDto customerDto,
                             RedirectAttributes redirectAttributes) {

        CustomerDto response = customerService.update(customerDto);

        if (!response.isSuccess()) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, response.getMessage());
            return "redirect:/customer/get?phoneNo=" + customerDto.getPhoneNo();
        }

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Customer updated successfully!");
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String phoneNo,
                         RedirectAttributes redirectAttributes) {

        try {
            customerService.delete(phoneNo);
            redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Customer deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Failed to delete customer!");
        }

        return REDIRECT_CUSTOMER_LIST;
    }

    @PostMapping("/toggle-status")
    @ResponseBody
    public void toggle(@RequestParam String identifier) {
        customerService.toggleStatus(identifier);
    }
}
