package com.ust.pos.address.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.AddressRepository;
import com.ust.pos.model.Address;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address existing = addressRepository.findByIdentifier(addressDto.getIdentifier());
        if (existing != null) {
            addressDto.setSuccess(false);
            addressDto.setMessage("Address already exists : " + addressDto.getIdentifier());
            return addressDto;
        }

        Address address = modelMapper.map(addressDto, Address.class);
        setAuditFields(address, true);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Address address = addressRepository.findByIdentifier(identifier);
        softDelete(address);
        setAuditFields(address,false);
        addressRepository.save(address);
    }

    @Override
    public AddressDto findByIdentifier(String identifier) {

        Address address = addressRepository.findByIdentifier(identifier);

        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        Address existing = addressRepository.findByIdentifier(addressDto.getIdentifier());
        if (existing == null) {
            addressDto.setSuccess(false);
            addressDto.setMessage("Address not found : " + addressDto.getIdentifier());
            return addressDto;
        }
        modelMapper.map(addressDto, existing);
        setAuditFields(existing,false);
        addressRepository.save(existing);
        return addressDto;
    }

    @Override
    public List<AddressDto> findAllByPhoneNumber(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNo(phoneNo), listType);
    }
}
