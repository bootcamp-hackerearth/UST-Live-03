package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    CustomerDto changeCustomerStatus(String identifier, boolean status);

    WsDto<CustomerDto> findAll(Pageable pageable);

    WsDto<CustomerDto> findAll(Specification<Customer> spec, Pageable pageable);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto findById(Long id);

    CustomerDto findByIdentifier(String identifier);

    void delete(String identifier);

}