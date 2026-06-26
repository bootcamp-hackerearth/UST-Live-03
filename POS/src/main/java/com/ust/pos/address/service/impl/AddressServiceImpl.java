package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.modell.Address;
import com.ust.pos.modell.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends BaseService implements AddressService {

    public static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException("Address not found");
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public AddressDto findByIdentifier(String identifier) {
        Address address = addressRepository.findByIdentifierAndDeletedFalse(identifier);
        return address == null ? null : modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> findAllByPhoneNo(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNoAndDeletedFalse(phoneNo), listType);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        setCreatedDetails(address);
        address.setDeleted(false);
        addressRepository.save(address);
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        Address existingAddress = addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(addressDto.getPhoneNo(), addressDto.getAddressType());

        if (existingAddress == null) {
            throw RUNTIME_EXCEPTION;
        }
        modelMapper.map(addressDto, existingAddress);
        setModifiedDetails(existingAddress);
        addressRepository.save(existingAddress);
        return modelMapper.map(existingAddress, AddressDto.class);
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNoAndDeletedFalse(phoneNo);
        addresses.forEach(this::softDelete);
        addressRepository.saveAll(addresses);
        return true;
    }

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByDeletedFalse(), listType);
    }
}