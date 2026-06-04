package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    List<CustomerDto> findAll(Pageable pageable);

    CustomerDto save(CustomerDto customerDto);

    void delete(String identifier);

    void updateStatus(String identifier);

    List<CustomerDto> findAllActive();

    CustomerDto findByIdentifier(String identifier);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto changeToggleStatus(String identifier, boolean status);
}
