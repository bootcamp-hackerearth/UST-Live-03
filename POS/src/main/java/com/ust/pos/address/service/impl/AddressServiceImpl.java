package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.modell.Address;
import com.ust.pos.modell.AddressRepository;
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
    public AddressDto findByIdentifier(String identifier) {
        return modelMapper.map(addressRepository.findByIdentifier(identifier), AddressDto.class);
    }

    @Override
    public List<AddressDto> findAllByPhoneNo(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNo(phoneNo), listType);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        String phoneNo = addressDto.getPhoneNo();
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressDto.getAddressType());
        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);
        return addressDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> address = addressRepository.findAllByPhoneNo(phoneNo);
        addressRepository.deleteAll(address);
        return true;
    }

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }

}