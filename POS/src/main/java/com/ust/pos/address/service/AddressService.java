package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;
import java.util.List;

public interface AddressService {
    List<AddressDto> findAll();

    AddressDto save(AddressDto addressDto);

    void delete(String identifier);

    AddressDto findByIdentifier(String identifier);

    AddressDto update(AddressDto addressDto);

    List<AddressDto> findAllByPhoneNumber(String phoneNo);
}