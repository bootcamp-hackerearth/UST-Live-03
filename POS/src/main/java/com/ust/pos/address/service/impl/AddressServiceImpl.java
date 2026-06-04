package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
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
        String identifier = addressDto.getIdentifier();
        Address existingAddress = addressRepository.findByIdentifier(identifier);
        if (existingAddress != null) {
            addressDto.setMessage("Address with identifier - " + identifier + " already exists");
            addressDto.setSuccess(false);
            return addressDto;
        }
        Address address = modelMapper.map(addressDto, Address.class);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        String identifier = addressDto.getIdentifier();
        Address existingAddress = addressRepository.findByIdentifier(identifier);
        if (existingAddress == null) {
            addressDto.setMessage("Address with identifier - " + identifier + " not found");
            addressDto.setSuccess(false);
            return addressDto;
        }
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
    public List<AddressDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        Page<Address> addressPage = addressRepository.findAll(pageable);
        return modelMapper.map(addressPage.getContent(), listType);
    }
}
