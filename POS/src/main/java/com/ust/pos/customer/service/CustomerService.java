package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto delete(String identifier, Long phoneNo);

    PaginatedResponseDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    List<CustomerDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable);
}