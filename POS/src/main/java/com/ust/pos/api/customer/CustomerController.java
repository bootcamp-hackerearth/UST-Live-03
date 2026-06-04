package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("customerApiController")
@RequestMapping("/api/customers")
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/list")
    public ResponseEntity<List<CustomerDto>> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        List<CustomerDto> customers = customerService.findAll(pageable);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CustomerDto> getByIdentifier(@PathVariable String identifier) {
        CustomerDto response = customerService.findByIdentifierWithAddressDto(identifier);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<CustomerDto> save(@RequestBody CustomerDto customerDto) {
        CustomerDto response = customerService.save(customerDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{identifier}")
    public ResponseEntity<CustomerDto> update(@PathVariable String identifier, @RequestBody CustomerDto customerDto) {
        customerDto.setIdentifier(identifier);
        CustomerDto response = customerService.update(customerDto);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete/{identifier}")
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