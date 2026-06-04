package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/customer/list";

    @Autowired
    private CustomerService customerService;

    @PostMapping("/list")
    public List<CustomerDto> list(@RequestBody PaginationDto pagination) {
        Pageable pageable = getPageable(pagination.getPage(), pagination.getSizePerPage(),
                pagination.getSortDirection(), pagination.getSortfield());
        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @GetMapping("/get")
    public CustomerDto updatePage(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @PostMapping("/delete")
    public CustomerDto delete(@RequestBody CustomerDto customerDto) {
        CustomerDto response = new CustomerDto();
        try {
            customerService.delete(customerDto.getIdentifier());
            response.setSuccess(true);
            response.setMessage("Customer deleted successfully");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Delete failed");
        }
        return response;
    }

    @GetMapping("/active")
    public List<CustomerDto> getActiveCustomers() {
        return customerService.findActive();
    }

    @PostMapping("/toggle-status")
    public CustomerDto toggleStatus(@RequestBody CustomerDto customerDto) {
        return customerService.toggleStatus(customerDto.getIdentifier());
    }
}