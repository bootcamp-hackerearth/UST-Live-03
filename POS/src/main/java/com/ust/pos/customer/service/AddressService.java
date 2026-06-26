package com.ust.pos.customer.service;

import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;

import java.util.List;

public interface AddressService {

    AddressDto save(AddressDto addressDto);

    AddressDto update(AddressDto addressDto);

    List<AddressDto> findAll();

    List<Address> findByPhoneNo(Long phoneNo);

    void deleteByPhoneNo(Long phoneNo);

    AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType);
}
