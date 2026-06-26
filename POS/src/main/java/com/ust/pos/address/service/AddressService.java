package com.ust.pos.address.service;

import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
public interface AddressService {

    AddressDto save(AddressDto addressDto);
    AddressDto update(AddressDto addressDto);
    boolean delete(String identifier);
    WsDto<AddressDto> findAll(Pageable pageable);
    AddressDto findByIdentifier(String identifier);
    List<AddressDto> findAllByPhoneNo(String phoneNo);

}
