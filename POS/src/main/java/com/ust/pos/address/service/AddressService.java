package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;
import java.util.List;

public interface AddressService {
    void save(AddressDto shipping, AddressDto billing);

    void update(AddressDto shipping, AddressDto billing);

    void delete(String identifier);

    List<AddressDto> findAll();

    AddressDto findByIdentifierAndShipping(String identifier);

    AddressDto findByIdentifierAndBilling(String identifier);
}
