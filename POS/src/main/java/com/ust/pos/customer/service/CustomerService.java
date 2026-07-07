package com.ust.pos.customer.service;


import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String identifier);

    WsDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto findById(String identifier);

    CustomerDto findByIdentifierWithAddressDto(String phoneNo);

    CustomerDto toggleStatus(String identifier);

    List<CustomerDto> findIfTrue();

    WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable);
}