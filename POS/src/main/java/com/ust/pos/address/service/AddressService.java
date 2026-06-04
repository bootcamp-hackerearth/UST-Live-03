package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;

public interface AddressService {

    void save(AddressDto addressDto);

    AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType);

    void delete(String phoneNo);
}