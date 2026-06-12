package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    PaginationResponseDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    void delete(String identifier);

    CustomerDto toggleStatus(String identifier, boolean status);
}