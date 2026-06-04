package com.ust.pos.customer.service;

import com.ust.pos.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> findAll();

    AddressDto save(AddressDto addressDto);

    AddressDto update(AddressDto addressDto);

    AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType);

    void deleteByPhoneNo(String phoneNo);
}
