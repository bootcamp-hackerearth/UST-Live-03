package com.ust.pos.customer.service;

import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    void delete(String identifier);

    List<CustomerDto> findAll(Pageable pageable);

    String buildAddressIdentifier(AddressDto address);

    CustomerDto findByIdentifier(String identifier);
}