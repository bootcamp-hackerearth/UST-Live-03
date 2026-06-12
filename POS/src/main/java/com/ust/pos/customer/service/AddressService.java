package com.ust.pos.customer.service;

import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface AddressService {
    AddressDto save(AddressDto addressDto);

    AddressDto update(AddressDto addressDto);

    WsDto<AddressDto> findAll(Pageable pageable);

    AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType);

    void deleteByPhoneNo(Long phoneNo);
}
