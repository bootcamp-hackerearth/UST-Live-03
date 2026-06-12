package com.ust.pos.api.customer;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.api.BaseController;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/customer")
public class CustomerControllerApi extends BaseController {
    @Autowired
    CustomerService customerService;
    @Autowired
    AddressService addressService;

    @PostMapping("/list")
    public WsDto<CustomerDto> list(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return customerService.findAll(pageable);

    }


    @PostMapping("/add")
    public CustomerDto save(@RequestBody CustomerDto customerDto) {
        return customerService.save(customerDto);

    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            customerService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    @GetMapping("/update")
    public CustomerDto update(@RequestParam String identifier) {
        return customerService.findByIdentifier(identifier);

    }

    @PostMapping("/update")
    public CustomerDto update(@RequestBody CustomerDto customerDto) {
        return customerService.update(customerDto);
    }

    @PostMapping("/changeStatus")
    public CustomerDto changestatus(@RequestBody CustomerDto customerDto) {
        return customerService.changeCustomerStatus(customerDto.getIdentifier(), customerDto.isStatus());
    }
}
