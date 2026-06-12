package com.ust.pos.customer.service.impl;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType) {

        Address address = addressRepository
                .findByPhoneNoAndAddressType(phoneNo, addressType);

        if (address == null) {
            return new AddressDto(); // return empty DTO instead of crashing
        }

        return modelMapper.map(address, AddressDto.class);
    }


    @Override
    public AddressDto save(AddressDto addressDto) {

        Address existingAddress = addressRepository.
                findByPhoneNoAndAddressType(addressDto.getPhoneNo(),
                        addressDto.getAddressType());

        if (existingAddress != null) {
            addressDto.setMessage("Address with identifier - " + addressDto.getAddressType() + " already exists");
            addressDto.setSuccess(false);
            return addressDto;
        }

        Address address = modelMapper.map(addressDto, Address.class);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {

        Address existingAddress = addressRepository.
                findByPhoneNoAndAddressType(addressDto.getPhoneNo(),
                        addressDto.getAddressType());

        if (existingAddress == null) {
            addressDto.setMessage("Address with identifier - " + addressDto.getAddressType() + " not found");
            addressDto.setSuccess(false);
            return addressDto;
        }

        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);

        return addressDto;
    }

    @Override
    public WsDto<AddressDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();

        Page<Address> addressPage = addressRepository.findAll(pageable);

        List<AddressDto> addressDtos = modelMapper.map(
                addressPage.getContent(),
                listType
        );

        WsDto<AddressDto> wsDto =
                new WsDto<>();

        wsDto.setContent(addressDtos);
        wsDto.setPage(addressPage.getNumber());
        wsDto.setSizePerPage(addressPage.getSize());
        wsDto.setTotalPages(addressPage.getTotalPages());
        wsDto.setTotalRecords(addressPage.getTotalElements());

        return wsDto;
    }

    @Override
    public void deleteByPhoneNo(Long phoneNo) {
        addressRepository.deleteByPhoneNo(phoneNo);

    }

}
