package com.ust.pos.api.customer;

import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'Cashier')")
public class CustomerApiController extends BaseController {

    private final CustomerService customerService;

    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/list")
    public WsDto<CustomerDto> home(@RequestBody PaginationDto paginationDto) throws Exception {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());

        //Global search
        Customer probe = new Customer();
        probe.setEmail(paginationDto.getSearch());
        probe.setAddress(paginationDto.getSearch());
        probe.setPartytype(paginationDto.getSearch());

        try{
            probe.setPhoneno(Long.valueOf(paginationDto.getSearch()));
        }
        catch(NumberFormatException e)
        {
            probe.setPhoneno(null);
        }
        Example<Customer> example = buildSearchProbe(Customer.class, paginationDto.getSearch());

        Page<CustomerDto> pageResult = customerService.findAll(example, pageable);//paginationDto.getSearch()

        WsDto<CustomerDto> response = new WsDto<>();

        response.setDtoList(pageResult.getContent());
        response.setPage(pageResult.getNumber());
        response.setSizePerPage(pageResult.getSize());
        response.setTotalPage(pageResult.getTotalPages());
        response.setTotalRecords(pageResult.getTotalElements());

        return response;
    }

    @GetMapping("/list")
    public List<CustomerDto> list()
    {
        return customerService.findAll();
    }

    @PostMapping("/add")
    public CustomerDto addPost(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);
    }

    @GetMapping("/get")
    public CustomerDto update(@RequestParam String identifier)
    {
        return customerService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CustomerDto updatePost(@RequestBody CustomerDto customerDto)
    {
        return customerService.update(customerDto);
    }

    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam String identifier)
    {
        try
        {
            customerService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    @GetMapping("/toggleStatus")
    public Boolean toggleStatus(@RequestParam String email)
    {
        try {
            customerService.toggleStatus(email);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
