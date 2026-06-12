package com.ust.pos.customer;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController extends BaseController {

    private static final String CUSTOMER_LIST = "customer/list";
    private static final String CUSTOMER_ADD = "customer/add";
    private static final String CUSTOMER_VIEW = "customer/customer";
    private static final String REDIRECT_CUSTOMER_LIST = "redirect:/customer/list";

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(),paginationDto.getSortField());
        model.addAttribute("customers", customerService.findAll(pageable));
        return CUSTOMER_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CustomerDto customerDto) {
        model.addAttribute("customers", customerService.findAll(null));
        return CUSTOMER_ADD;
    }

    @PostMapping("/add")
    public String addPost(Model model,@ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
            return CUSTOMER_ADD;
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        model.addAttribute("customerDto", response);
        return CUSTOMER_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(Model model,@ModelAttribute CustomerDto customerDto) {
        CustomerDto response = customerService.update(customerDto);
        if (!response.isSuccess()) {
            model.addAttribute("message", response.getMessage());
        }
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        customerService.delete(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }

    @PostMapping("/toggleStatus")
    public String toggleStatus(@RequestParam String identifier, boolean status) {
        customerService.toggleStatus(identifier, status);
        return REDIRECT_CUSTOMER_LIST;
    }
}