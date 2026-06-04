package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;

import java.util.List;

public interface AddressService {

    AddressDto save(AddressDto addressDto);

    AddressDto update(AddressDto addressDto);

    void delete(String identifier);

    List<AddressDto> findAll();

    AddressDto findByIdentifier(String identifier);

    List<AddressDto> findByPhoneNo(String email);

}
