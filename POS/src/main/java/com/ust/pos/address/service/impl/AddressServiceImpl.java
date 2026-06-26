package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl extends BaseService implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository,
                              ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        addressRepository.save(modelMapper.map(addressDto, Address.class));
        addressDto.setMessage("Successfully added the address");
        addressDto.setSuccess(true);
        return addressDto;
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType) {
        Address address = addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(phoneNo, addressType);
        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
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
        Address billing = addressRepository.findByPhoneNoAndAddressType(phoneNo,"billing");
        if (billing != null) {
            softDelete(billing);
            setModifiedDetails(billing);
            addressRepository.save(billing);
        }
        Address shipping = addressRepository.findByPhoneNoAndAddressType(phoneNo,"shipping");
        if (shipping != null) {
            softDelete(shipping);
            setModifiedDetails(shipping);
            addressRepository.save(shipping);
        }
    }
}