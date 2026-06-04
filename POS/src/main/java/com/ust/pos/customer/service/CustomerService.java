package com.ust.pos.customer.service;


import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String identifier);

    List<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    CustomerDto findByIdentifierWithAddressDto(String identifier);

    CustomerDto toggleStatus(String identifier);
}
