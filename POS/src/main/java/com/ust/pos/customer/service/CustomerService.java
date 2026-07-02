package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto findByIdentifier(String identifier);

    WsDto<CustomerDto> findAll(Pageable pageable);

    List<CustomerDto> findAll();

    void deleteByIdentifier(String identifier);

    Page<CustomerDto> findAll(Pageable pageable, String search);

}
