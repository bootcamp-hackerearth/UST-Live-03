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
public class CustomerControllerApi extends BaseController {

    public static final String REDIRECT_ROLE_LIST = "redirect:/customer/list";

    private final CustomerService customerService;

    public CustomerControllerApi(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public WsDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortfield());

        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Customer> example = buildGlobalSearchSpec(Customer.class, paginationDto.getKeyword());
            if (example != null) {
                return customerService.findAll(example, pageable);
            }
        }

        return customerService.findAll(pageable);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CustomerDto updatePage(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
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

    @PatchMapping("/toggle")
    @PreAuthorize("hasAnyAuthority( 'Manager','Admin')")
    public CustomerDto toggleStatus(@RequestBody CustomerDto customerDto) {
        return customerService.toggleStatus(customerDto.getIdentifier());
    }

    @GetMapping("/search")
    public List<CustomerDto> searchCustomer(@RequestParam String query){
        return customerService.searchCustomer(query);
    }
}