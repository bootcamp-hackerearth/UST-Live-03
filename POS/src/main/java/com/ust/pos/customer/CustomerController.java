package com.ust.pos.customer;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
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
        model.addAttribute("customers", customerService.findAll(getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField())));
        return CUSTOMER_LIST;
    }

    @GetMapping("/add")
    public String add(Model model, @ModelAttribute CustomerDto customerDto) {
        return CUSTOMER_ADD;
    }

    @PostMapping("/add")
    public String addPost(@ModelAttribute CustomerDto customerDto) {
        customerService.save(customerDto);
        return REDIRECT_CUSTOMER_LIST;
    }

    @PostMapping("/toggle")
    @ResponseBody
    public CustomerDto toggleStatus(@RequestBody CustomerDto dto) {
        return customerService.updateStatus(dto.getIdentifier(), dto.isStatus());
    }

    @GetMapping("/get")
    public String update(@RequestParam String identifier, Model model) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        model.addAttribute("customerDto", response);
        return CUSTOMER_VIEW;
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute CustomerDto customerDto) {
        customerService.update(customerDto);
        return REDIRECT_CUSTOMER_LIST;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam String identifier) {
        customerService.delete(identifier);
        return REDIRECT_CUSTOMER_LIST;
    }
}
