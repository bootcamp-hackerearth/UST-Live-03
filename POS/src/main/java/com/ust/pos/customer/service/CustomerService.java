package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto findByIdentifier(String identifier);

    List<CustomerDto> findAll();

    void deleteByIdentifier(String identifier);

    Page<CustomerDto> findAll(String identifier, Pageable pageable);

}
