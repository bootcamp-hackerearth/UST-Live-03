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

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void save(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        setCreatedDetails(address);
        addressRepository.save(address);
        addressDto.setMessage("Successfully added the address");
        addressDto.setSuccess(true);
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType) {
        return modelMapper.map(addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType), AddressDto.class);
    }

    @Override
    public void update(AddressDto addressDto) {
        String phoneNo = addressDto.getPhoneNo();
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressDto.getAddressType());
        setModifiedDetails(existingAddress);
        modelMapper.map(addressDto, existingAddress);
        addressRepository.save(existingAddress);
    }

    @Override
    public void delete(String phoneNo) {
        addressRepository.deleteByPhoneNo(phoneNo);
    }
}
