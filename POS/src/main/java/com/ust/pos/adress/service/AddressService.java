package com.ust.pos.adress.service;

import com.ust.pos.dto.AddressDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AddressService {
    AddressDto save(AddressDto addressDto);

    List<AddressDto> findAll(Pageable pageable);

    AddressDto findByIdentifier(String identifier);

    List<AddressDto> findAllByPhoneNumber(String phoneNo);

    AddressDto update(AddressDto addressDto);

    boolean delete(String phoneNo);
}
