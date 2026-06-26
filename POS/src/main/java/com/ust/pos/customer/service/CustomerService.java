package com.ust.pos.customer.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PageDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CustomerService {
    CustomerDto save(CustomerDto customerDto);

    CustomerDto update(CustomerDto customerDto);

    boolean delete(String username);

    PageDto<CustomerDto> findAll(Pageable pageable);

    CustomerDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<CustomerDto> findActiveCustomers();
}