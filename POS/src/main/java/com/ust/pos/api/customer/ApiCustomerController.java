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
public class ApiCustomerController extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/customer/list";
    @Autowired
    private CustomerService customerService;

    @PostMapping("/list")
    public List<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return customerService.findAll(pageable);
    }


    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);


    }

    @GetMapping("/get")
    public CustomerDto update(@RequestParam String identifier) {

        return customerService.findByIdentifierWithAddressDto(identifier);
    }

    @PostMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {

        return customerService.update(customerDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            customerService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle-status")
    public CustomerDto toggle(@RequestParam String identifier) {

        return customerService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<CustomerDto> findByStatus() {

        return customerService.findIfTrue();
    }

    @GetMapping("/findByIdentifier")
    public CustomerDto findByIdentifier(@RequestParam String identifier) {

        return customerService.findById(identifier);
    }

}

