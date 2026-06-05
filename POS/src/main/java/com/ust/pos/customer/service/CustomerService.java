package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    void delete(String identifier);

    CustomerDto toggleStatus(String identifier, boolean status);
}