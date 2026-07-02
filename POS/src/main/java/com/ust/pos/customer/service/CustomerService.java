package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String username);

    PageDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    PageDto<CustomerDto> findAll(Specification<Customer> spec, Pageable pageable, String keyword);

    void toggleStatus(String identifier);

    List<CustomerDto> findActiveCustomers();

}