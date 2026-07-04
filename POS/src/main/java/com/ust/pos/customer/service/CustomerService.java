package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomerService {

    CustomerDto findByIdentifier(String identifier);

    CustomerDto findByIdentifierWithAddressDto(String identifier);

    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String identifier);

    WsDto<CustomerDto> findAll(Pageable pageable);

    WsDto<CustomerDto> findAll(Specification<Customer> spec, Pageable pageable, String keyword);

    CustomerDto toggleStatus(String identifier);
}