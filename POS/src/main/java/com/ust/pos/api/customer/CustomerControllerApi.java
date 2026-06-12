package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerControllerApi extends BaseController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @PostMapping("/list")
    public List<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @GetMapping("/get")
    public CustomerDto update(@RequestParam String identifier) {
        CustomerDto response = customerService.findByIdentifier(identifier);
        response.setBillingAddress(addressService.findByPhoneNoAndAddressType(response.getPhoneNo(), "billingAddress"));
        response.setShippingAddress(addressService.findByPhoneNoAndAddressType(response.getPhoneNo(), "shippingAddress"));
        return response;
    }

    @PostMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier, Long phoneNo) {
        try {
            customerService.delete(identifier, phoneNo);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}


