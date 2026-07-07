package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/customer")
public class ApiCustomerController extends BaseController {

    private final CustomerService customerService;

    public ApiCustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','SUPPORT')")
    public WsDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Customer> example = buildGlobalSearchSpec(Customer.class, paginationDto.getKeyword());
            if (example != null) {
                return customerService.findAll(example, pageable);
            }
        }

        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public CustomerDto addcustomer(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);

    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public boolean delete(@RequestParam String identifier) {
        try {
            customerService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public CustomerDto update(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public CustomerDto updateCustomer(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @PostMapping("/toggle")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public CustomerDto toggle(@RequestBody CustomerDto customerDto) {
        return customerService.changeToggleStatus(customerDto.getIdentifier(), customerDto.isStatus());
    }

    @PostMapping("/findActiveStatus")
    public List<CustomerDto> findActive() {
        return customerService.findActiveStatus();
    }

}
