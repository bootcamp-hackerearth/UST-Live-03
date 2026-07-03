package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {
    WsDto<CustomerDto> findAll(Pageable pageable);

    WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable);

    CustomerDto save(CustomerDto customerDto);

    boolean delete(String identifier);

    void updateStatus(String identifier);

    List<CustomerDto> findAllActive();

    CustomerDto findByIdentifier(String identifier);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto changeToggleStatus(String identifier, boolean status);

    List<CustomerDto> findActiveStatus();
}
