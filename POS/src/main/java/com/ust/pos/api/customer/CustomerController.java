package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("customerApiController")
@RequestMapping("/api/customers")
public class CustomerController extends BaseController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/list")
    public WsDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Customer> spec = buildGlobalSearchSpec(Customer.class, paginationDto.getKeyword());
            return customerService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return customerService.findAll(pageable);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CustomerDto> getByIdentifier(@PathVariable String identifier) {
        try {
            CustomerDto response = customerService.findByIdentifierWithAddressDto(identifier);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<CustomerDto> save(@RequestBody CustomerDto customerDto) {
        customerDto.setSuccess(true);
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{identifier}")
    public ResponseEntity<CustomerDto> update(@PathVariable String identifier, @RequestBody CustomerDto customerDto) {
        customerDto.setIdentifier(identifier);
        customerDto.setSuccess(true);
        CustomerDto response = customerService.update(customerDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{identifier}")
    public ResponseEntity<Boolean> delete(@PathVariable String identifier) {
        try {
            boolean deleted = customerService.delete(identifier);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/toggle/{identifier}")
    public ResponseEntity<CustomerDto> toggleStatus(@PathVariable String identifier) {
        CustomerDto response = customerService.toggleStatus(identifier);
        return ResponseEntity.ok(response);
    }
}