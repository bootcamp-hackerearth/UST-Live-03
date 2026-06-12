package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
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
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType) {
        if (phoneNo == null || phoneNo == 0) {
            return new AddressDto();
        }
        Address address = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType);
        if (address == null) {
            return new AddressDto();
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        if (addressDto.getPhoneNo() == null || addressDto.getPhoneNo() == 0) {
            addressDto.setSuccess(false);
            addressDto.setMessage("Phone number is required to save address");
            return addressDto;
        }
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(
                addressDto.getPhoneNo(), addressDto.getAddressType());
        if (existingAddress != null) {
            addressDto.setSuccess(false);
            addressDto.setMessage("Address already exists");
            return addressDto;
        }
        Address address = modelMapper.map(addressDto, Address.class);
        address.setId(null);
        addressRepository.save(address);
        addressDto.setSuccess(true);
        addressDto.setMessage("Address saved successfully");
        return addressDto;
    }

    public AddressDto update(AddressDto addressDto) {
        if (addressDto.getPhoneNo() == null || addressDto.getPhoneNo() == 0) {
            addressDto.setSuccess(false);
            addressDto.setMessage("Phone number is required to update address");
            return addressDto;
        }
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(
                addressDto.getPhoneNo(), addressDto.getAddressType());
        if (existingAddress == null) {
            addressDto.setSuccess(false);
            addressDto.setMessage("Address not found");
            return addressDto;
        }
        Long existingId = existingAddress.getId();
        modelMapper.map(addressDto, existingAddress);
        existingAddress.setId(existingId);
        addressRepository.save(existingAddress);
        addressDto.setSuccess(true);
        addressDto.setMessage("Address updated successfully");
        return addressDto;
    }

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }
}