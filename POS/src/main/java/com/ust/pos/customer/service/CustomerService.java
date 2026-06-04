package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    List<CustomerDto> findAll(Pageable pageable);

    List<CustomerDto> findAllActive();

    CustomerDto findByIdentifier(String identifier);

    CustomerDto toggleStatus(String identifier);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String identifier);

}
