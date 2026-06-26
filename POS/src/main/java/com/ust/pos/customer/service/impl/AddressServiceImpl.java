package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends BaseService implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public AddressDto findByPhoneNoAndAddressType(Long phoneNumber, String addressType) {
        Address address = addressRepository.findByPhoneNumberAndAddressTypeAndDeletedFalse(phoneNumber, addressType);
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
        addressDto.setSuccess(true);
        addressDto.setMessage("Address saved successfully");
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        Address existingAddress =
                addressRepository.findByPhoneNumberAndAddressTypeAndDeletedFalse(addressDto.getPhoneNumber(), addressDto.getAddressType());
        if (existingAddress == null) {
            return save(addressDto);
        }
        modelMapper.map(addressDto, existingAddress);
        setModifiedDetails(existingAddress);
        addressRepository.save(existingAddress);
        addressDto.setSuccess(true);
        addressDto.setMessage("Address updated successfully");
        return addressDto;
    }

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findByDeletedFalse(), listType);
    }

    @Override
    public void deleteByPhoneNumber(Long phoneNumber) {
        List<Address> addressList = addressRepository.findByPhoneNumberAndDeletedFalse(phoneNumber);
        for (Address address : addressList) {
            softDelete(address);
            setModifiedDetails(address);
        }
        addressRepository.saveAll(addressList);
    }

}



