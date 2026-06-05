package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;

public interface AddressService {

    AddressDto save(AddressDto addressDto);

    AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType);

    void update(AddressDto addressDto);

    void delete(String identifier);
}