package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerApiController extends BaseController {

    private final CustomerService customerService;

    private final AddressService addressService;

    public CustomerApiController(CustomerService customerService, AddressService addressService) {
        this.customerService = customerService;
        this.addressService = addressService;
    }

    @PostMapping("/list")
    public WsDto<CustomerDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {

        return customerService.save(customerDto);
    }

    @GetMapping("/getaddressbyphonenoandaddresstype")
    public AddressDto findAddress(Long phoneNo, String addressType) {

        return addressService.findByPhoneNoAndAddressType(phoneNo, addressType);
    }

    @GetMapping("/{identifier}")
    public CustomerDto update(@PathVariable String identifier) {

        CustomerDto response = customerService.findByIdentifier(identifier);
        response.setBillingAddress(findAddress(response.getPhoneNo(), "billingAddress"));
        response.setShippingAddress(findAddress(response.getPhoneNo(), "shippingAddress"));

        return response;
    }

    @PutMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {

        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();
        Long phoneNo = customerDto.getPhoneNo();

        try {
            customerService.delete(identifier, phoneNo);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}


