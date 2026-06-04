package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface AddressService {
    AddressDto save(AddressDto addressDto);

    AddressDto update(AddressDto addressDto);

    boolean delete(String identifier);

    List<AddressDto> findAll();

    AddressDto findByIdentifier(String identifier);

    List<AddressDto> findAllByPhoneNo(String phoneNo);
}
