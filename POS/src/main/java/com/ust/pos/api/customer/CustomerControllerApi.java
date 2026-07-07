package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Customer;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerControllerApi extends BaseController {

    private final CustomerService customerService;

    @PostMapping("/list")
    public PaginatedResponseDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
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
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @GetMapping("/get")
    public CustomerDto get(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    public CustomerDto delete(@RequestBody CustomerDto customerDto) {
        try {
            return customerService.delete(customerDto.getIdentifier(), customerDto.getPhoneNo());
        } catch (Exception e) {
            CustomerDto errorDto = new CustomerDto();
            errorDto.setSuccess(false);
            errorDto.setMessage("Delete failed: " + e.getMessage());
            return errorDto;
        }
    }

    @PatchMapping("/toggle")
    public boolean changeStatus(@RequestBody CustomerDto customerDto) {
        try {
            customerService.changeStatus(customerDto.getIdentifier(), customerDto.getStatus());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/active")
    public List<CustomerDto> getActiveCustomers() {
        return customerService.findAllActive();
    }
}