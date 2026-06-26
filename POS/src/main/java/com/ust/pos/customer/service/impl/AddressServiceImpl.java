package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AddressServiceImpl extends BaseService implements AddressService {

    private final AddressRepository addressRepository;

    private final ModelMapper modelMapper;


    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType) {

        Address address = addressRepository.
                findByPhoneNoAndAddressType(phoneNo, addressType);

        if (address == null) {
            return null;
        }

        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {

        Address address = modelMapper.map(addressDto, Address.class);
        setCreatedDetails(address);
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
        setModifiedDetails(existingAddress);
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
    public List<Address> findByPhoneNo(Long phoneNo) {
        return addressRepository.findByPhoneNo(phoneNo);
    }

    @Override
    public void deleteByPhoneNo(Long phoneNo) {

        addressRepository.deleteByPhoneNo(phoneNo);
    }

}



