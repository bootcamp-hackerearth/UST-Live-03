package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    CustomerDto changeCustomerStatus(String identifier, boolean status);

    WsDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto update(CustomerDto customerDto);

    CustomerDto findById(Long id);

    CustomerDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

}
