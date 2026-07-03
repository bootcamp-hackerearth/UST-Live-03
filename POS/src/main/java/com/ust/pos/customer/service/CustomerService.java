package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    WsDto<CustomerDto> findAll(Pageable pageable);

    WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable);

    List<CustomerDto> findAllActive();

    CustomerDto findByIdentifier(String identifier);

    CustomerDto toggleStatus(String identifier);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String identifier);

}
