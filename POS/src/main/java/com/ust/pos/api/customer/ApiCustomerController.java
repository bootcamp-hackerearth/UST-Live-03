package com.ust.pos.api.customer;

import com.ust.pos.address.service.AddressService;
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

    @Autowired
    CustomerService customerService;

    @Autowired
    AddressService addressService;

    @PostMapping("/list")
    public List<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    public CustomerDto add(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
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

    @GetMapping("/get")
    public CustomerDto update(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CustomerDto updatePrice(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @PostMapping("/toggle")
    public CustomerDto toggle(@RequestBody CustomerDto customerDto) {
        return customerService.changeToggleStatus(customerDto.getIdentifier(), customerDto.isStatus());
    }
}
