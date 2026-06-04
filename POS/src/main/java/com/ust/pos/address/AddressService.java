package com.ust.pos.address;

import com.ust.pos.dto.AddressDto;

public interface AddressService {
    AddressDto save(AddressDto addressDto);

    AddressDto findByPhoneAndAddressType(String phoneNo, String billing);

    void delete(String phoneNo);

    void update(AddressDto addressDto);
}
