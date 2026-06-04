package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;

public interface AddressService {
    AddressDto save(AddressDto addressDto);

    AddressDto findByPhoneAndAddressType(String phoneNo, String billing);

    void delete(String identifier);

    void update(AddressDto addressDto);
}