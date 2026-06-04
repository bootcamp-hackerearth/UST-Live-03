package com.ust.pos.customer.service.impl;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {

        Address existingAddress =
                addressRepository.findByPhoneNoAndAddressType(
                        addressDto.getPhoneNo(),
                        addressDto.getAddressType()
                );
        if (existingAddress == null) {
            existingAddress = new Address();
        }

        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);

        return addressDto;
    }


    @Override
    public AddressDto update(AddressDto addressDto) {

        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(addressDto.getPhoneNo(), addressDto.getAddressType());
        if (existingAddress == null) {
            addressDto.setMessage("Address with address - " + addressDto.getAddressType() + " not found");
            addressDto.setSuccess(false);
            return addressDto;
        }
        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);
        return addressDto;
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType) {

        Address address = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType);

        if (address == null) {
            return new AddressDto();
        }

        return modelMapper.map(address, AddressDto.class);

    }

    @Override
    public void deleteByPhoneNo(String phoneNo) {
        addressRepository.deleteByPhoneNo(phoneNo);
    }
}
