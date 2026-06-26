package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import jakarta.transaction.Transactional;
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
    public AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType) {
        Address address = addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(phoneNo, addressType);
        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    @Transactional
    public void deleteByPhone(Long phoneNo) {
        List<Address> addressList = addressRepository.findByPhoneNoAndDeletedFalse(phoneNo);
        for (Address address : addressList) {
            softDelete(address);
            setModifiedDetails(address);
        }
        addressRepository.saveAll(addressList);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address existingAddress = addressRepository.
                findByPhoneNoAndAddressTypeAndDeletedFalse(addressDto.getPhoneNo(), addressDto.getAddressType());

        if (existingAddress != null) {
            addressDto.setMessage("Address with identifier - " + addressDto.getAddressType() + " already exists");
            addressDto.setSuccess(false);
            return addressDto;
        }
        Address address = modelMapper.map(addressDto, Address.class);
        setCreatedDetails(address);
        setModifiedDetails(address);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        Address existingAddress = addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(addressDto.getPhoneNo(), addressDto.getAddressType());

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

}



