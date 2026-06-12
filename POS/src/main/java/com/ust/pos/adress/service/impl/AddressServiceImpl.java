package com.ust.pos.adress.service.impl;

import com.ust.pos.adress.service.AddressService;
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
    public static final String ADDRESS_WITH_IDENTIFIER = "Address with identifier ' ";
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

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
        Address savedAddress = addressRepository.findByIdentifier(identifier);
        modelMapper.map(savedAddress, addressDto);
        addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " 'added Successfully");
        addressDto.setSuccess(true);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        String identifier = addressDto.getIdentifier();
        Address existingAddress = addressRepository.findByIdentifier(addressDto.getIdentifier());
        if (existingAddress == null) {
            addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " 'not found");
            addressDto.setSuccess(false);
            return addressDto;
        }
        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);
        Address updatedAddress = addressRepository.findByIdentifier(identifier);
        modelMapper.map(updatedAddress, addressDto);
        addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " 'Updated");
        addressDto.setSuccess(true);
        return addressDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNo(phoneNo);
        addressRepository.deleteAll(addresses);
        return true;
    }

    @Override
    public List<AddressDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        Page<Address> addressPage = addressRepository.findAll(pageable);
        return modelMapper.map(addressPage.getContent(), listType);
    }

    @Override
    public AddressDto findByIdentifier(String identifier) {
        return modelMapper.map(addressRepository.findByIdentifier(identifier), AddressDto.class);
    }

    @Override
    public List<AddressDto> findAllByPhoneNumber(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNo(phoneNo), listType);
    }
}
