package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    CustomerDto changeCustomerStatus(String identifier, boolean status);

    List<CustomerDto> findAll(Pageable pageable);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto findById(Long id);

    CustomerDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

}
