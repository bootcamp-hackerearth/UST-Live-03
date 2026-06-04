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
    private AddressRepository addressRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDto findByPhoneNoAndAddressType(Long phoneNumber, String addressType) {
        Address address = addressRepository.findByPhoneNumberAndAddressType(phoneNumber, addressType);
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address existingAddress = addressRepository.findByPhoneNumberAndAddressType(addressDto.getPhoneNumber(), addressDto.getAddressType());
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
        Address existingAddress = addressRepository.findByPhoneNumberAndAddressType(addressDto.getPhoneNumber(), addressDto.getAddressType());
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
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }

    @Override
    public void deleteByPhoneNumber(Long phoneNumber) {
        addressRepository.deleteByPhoneNumber(phoneNumber);
    }

}
