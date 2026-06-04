package com.ust.pos.address.service.impl;

import com.ust.pos.address.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import jakarta.transaction.Transactional;
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
        Address address = modelMapper.map(addressDto, Address.class);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    @Transactional
    public void update(AddressDto addressDto) {

        String phoneNo = addressDto.getPhoneNo();
        String addressType = addressDto.getAddressType();

        Address existingAddress =
                addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType);

        if (existingAddress == null) {
            Address newAddress = modelMapper.map(addressDto, Address.class);
            addressRepository.save(newAddress);
            return;
        }

        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);
    }

    @Override
    @Transactional
    public void delete(String phoneNo) {
        addressRepository.deleteByPhoneNo(phoneNo);
    }

    @Override
    public AddressDto findByPhoneAndAddressType(String phoneNo, String addressType) {
        Address address =
                addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType);
        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
    }
}