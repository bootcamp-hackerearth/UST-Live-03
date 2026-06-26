package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerControllerApi extends BaseController {

    private final CustomerService customerService;

    public CustomerControllerApi(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public List<CustomerDto> all() {
        return customerService.findAll();
    }

    @PostMapping("/list")
    public WsDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortField());
        Page<CustomerDto> pageResult = customerService.findAll(pageable, paginationDto.getSearch());
        WsDto<CustomerDto> response = new WsDto<>();
        response.setContent(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPages(pageResult.getTotalPages());
        return response;
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
    public boolean delete(@RequestParam String identifier) {
        try {
            customerService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}