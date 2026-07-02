package com.ust.pos.api.customer;
import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.model.Customer;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customer")
public class CustomerControllerApi extends BaseController {

    private final CustomerService customerService;

    public CustomerControllerApi(CustomerService customerService){
        this.customerService=customerService;
    }

    @PostMapping("/list")
    public PageDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        if (StringUtils.isNotEmpty(paginationDto.getKeyword())) {
            Specification<Customer> spec = buildGlobalSearchSpec(Customer.class, paginationDto.getKeyword());
            return customerService.findAll(spec, pageable, paginationDto.getKeyword());
        }
        return customerService.findAll(pageable);
    }

    @GetMapping("/identifier")
    public CustomerDto getCustomerByIdentifier(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @PutMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            customerService.delete(identifier);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @PostMapping("/toggleStatus")
    public void toggleStatus(@RequestParam String identifier) {
        customerService.toggleStatus(identifier);
    }

    @GetMapping("/findByStatus")
    public List<CustomerDto> findByStatus() {
        return customerService.findActiveCustomers();
    }
}