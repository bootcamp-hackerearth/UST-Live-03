package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerApiController extends BaseController {

    private final CustomerService customerService;

    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/list")
    public List<CustomerDto> list() {
        return customerService.findAll();
    }

    @PostMapping("/list")
    public WsDto<CustomerDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(),
                paginationDto.getSortField());
        Page<CustomerDto> customer = customerService.findAll(pageable, paginationDto.getSearch());
        WsDto<CustomerDto> result = new WsDto<>();
        result.setContent(customer.getContent());
        result.setSizePerPage(customer.getSize());
        result.setPage(customer.getNumber());
        result.setTotalPages(customer.getTotalPages());
        return result;
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @GetMapping("/get")
    public CustomerDto update(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String email) {
        try {
            customerService.delete(email);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
