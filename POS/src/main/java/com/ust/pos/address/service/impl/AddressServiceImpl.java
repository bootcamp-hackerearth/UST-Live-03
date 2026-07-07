package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AddressServiceImpl extends CommonService implements AddressService {

    private static final String ADDRESS_WITH_IDENTIFIER = "Address with identifier - ";
    private static final String NOT_FOUND = " not found";
    private static final String ALREADY_EXISTS = " already exists";
    private static final String SOFT_DELETED = " has been soft deleted.(Rollback by changing status";

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    private String addressNotFound(String identifier) {
        return ADDRESS_WITH_IDENTIFIER + identifier + NOT_FOUND;
    }

    @Override
    public AddressDto findByIdentifier(String identifier) {
        Address address = requireResource(addressRepository.findByIdentifier(identifier), addressNotFound(identifier));

        return modelMapper.map(address, AddressDto.class);
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

            if (existingAddress.isDeleted()) {
                addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + SOFT_DELETED);

                addressDto.setSuccess(false);
                return addressDto;
            }

            addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + ALREADY_EXISTS);

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

        Address existingAddress = requireResource(addressRepository.findByIdentifier(identifier), addressNotFound(identifier));

        modelMapper.map(addressDto, existingAddress);

        setAuditFields(existingAddress, false);

        addressRepository.save(existingAddress);

        return addressDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNo(phoneNo);
        for (Address address : addresses) {
            softDelete(address);
            setAuditFields(address, false);
            addressRepository.save(address);
        }
        return true;
    }

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }
}