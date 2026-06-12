package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {

    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    void delete(String identifier, Long phoneNo);

    PaginatedResponseDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    List<CustomerDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
