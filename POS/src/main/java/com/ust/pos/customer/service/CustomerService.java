package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {
    PaginationResponseDto<CustomerDto> findAll(Pageable pageable);

    PaginationResponseDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto updateStatus(String identifier, boolean status);

    void delete(String identifier);

    List<CustomerDto> searchCustomer(String query);
}
