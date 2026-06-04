package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;

public interface AddressService {

    AddressDto save(AddressDto addressDto);

    AddressDto findById(Long id);

    AddressDto update(AddressDto addressDto);

    AddressDto findByIdentifierAndAddressType(String identifier, String addressType);

    AddressDto findByIdentifier(String identifier);
}