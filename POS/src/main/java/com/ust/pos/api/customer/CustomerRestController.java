package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
public class CustomerRestController extends BaseController {


    private final CustomerService customerService;

    private final AddressService addressService;

    public CustomerRestController(CustomerService customerService, AddressService addressService) {
        this.customerService = customerService;
        this.addressService = addressService;
    }

    @PostMapping("/list")
    public WsDto<CustomerDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Customer> example = buildGlobalSearchSpec(Customer.class, paginationDto.getKeyword());
            if (example != null) {
                return customerService.findAll(example, pageable);
            }
        }
        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @PostMapping("/get")
    public CustomerDto update(Model model, @RequestBody String identifier) {

        CustomerDto response = customerService.findByIdentifier(identifier);
        try {
            response.setBillingAddress(addressService.
                    findByPhoneNoAndAddressType(response.getPhoneNo(), "billingAddress"));
            response.setShippingAddress(addressService.
                    findByPhoneNoAndAddressType(response.getPhoneNo(), "shippingAddress"));
        } catch (Exception e) {
            return response;
        }
        return response;
    }

    @PutMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            customerService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/toggle")
    public String toggleStatus(@RequestBody String identifier) {
        customerService.toggleStatus(identifier);
        return identifier;
    }
}


