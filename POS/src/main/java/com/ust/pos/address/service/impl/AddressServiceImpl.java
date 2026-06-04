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
    public void save(AddressDto addressDto) {
        if (addressDto.getPhoneNo() == null || addressDto.getPhoneNo().isEmpty() ||
                addressDto.getAddressType() == null || addressDto.getAddressType().isEmpty() ||
                addressDto.getAddressLine() == null || addressDto.getAddressLine().isEmpty() ||
                addressDto.getCity() == null || addressDto.getCity().isEmpty() ||
                addressDto.getState() == null || addressDto.getState().isEmpty() ||
                addressDto.getZip() == null || addressDto.getZip().isEmpty() ||
                addressDto.getCountry() == null || addressDto.getCountry().isEmpty()) {
            addressDto.setSuccess(false);
            addressDto.setMessage("All address fields are required");
            return;
        }

        Address existing = addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc(
                addressDto.getPhoneNo(), addressDto.getAddressType());
        if (existing != null) {
            existing.setAddressLine(addressDto.getAddressLine());
            existing.setCity(addressDto.getCity());
            existing.setState(addressDto.getState());
            existing.setZip(addressDto.getZip());
            existing.setCountry(addressDto.getCountry());
            addressRepository.save(existing);
        } else {
            Address address = modelMapper.map(addressDto, Address.class);
            addressRepository.save(address);
        }
        addressDto.setSuccess(true);
        addressDto.setMessage("Address saved successfully");
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType) {
        Address address = addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc(phoneNo, addressType);
        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public void delete(String phoneNo) {
        addressRepository.deleteByPhoneNo(phoneNo);
    }
}