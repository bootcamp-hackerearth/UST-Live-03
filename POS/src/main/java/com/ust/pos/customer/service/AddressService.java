package com.ust.pos.customer.service;

import com.ust.pos.dto.AddressDto;

import java.util.List;

public interface AddressService {

    AddressDto save(AddressDto addressDto);

    AddressDto update(AddressDto addressDto);

    List<AddressDto> findAll();

    void deleteByPhoneNo(Long phoneNo);

    AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType);
}
