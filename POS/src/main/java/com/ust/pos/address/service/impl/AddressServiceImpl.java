package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AddressServiceImpl extends CommonService implements AddressService {
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDto findByIdentifier(String identifier) {
        return modelMapper.map(addressRepository.findByIdentifier(identifier), AddressDto.class);
    }

    @Override
    public List<AddressDto> findAllByPhoneNo(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNoAndDeletedFalse(phoneNo), listType);
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
        setAuditFields(address, true);
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
        setAuditFields(existingAddress, false);
        addressRepository.save(existingAddress);
        return addressDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNoAndDeletedFalse(phoneNo);
        if (addresses == null || addresses.isEmpty()) {
            return false;
        }
        for (Address address : addresses) {
            softDelete(address);
            setAuditFields(address, false);
        }
        addressRepository.saveAll(addresses);
        return true;
    }

    @Override
    public List<AddressDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        Page<Address> addressPage = addressRepository.findByDeletedFalse(pageable);
        return modelMapper.map(addressPage.getContent(), listType);
    }

}