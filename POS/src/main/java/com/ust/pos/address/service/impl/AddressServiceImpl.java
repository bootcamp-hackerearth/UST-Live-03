package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDto save(AddressDto addressDto) {
        addressRepository.save(modelMapper.map(addressDto, Address.class));
        addressDto.setMessage("Successfully added the address");
        addressDto.setSuccess(true);
        return addressDto;
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType) {
        return modelMapper.map(addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType), AddressDto.class);
    }

    @Override
    public void update(AddressDto addressDto) {
        String phoneNo = addressDto.getPhoneNo();
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressDto.getAddressType());
        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);
    }

    @Override
    public void delete(String phoneNo) {
        addressRepository.deleteByPhoneNo(phoneNo);
    }
}