package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {
    void save(AddressDto shipping, AddressDto billing);

    void update(AddressDto shipping, AddressDto billing);

    void delete(String identifier);

    List<AddressDto> findAll();

    List<AddressDto> findAll(Pageable pageable);

    AddressDto findByIdentifierAndShipping(String identifier);

    AddressDto findByIdentifierAndBilling(String identifier);
}
