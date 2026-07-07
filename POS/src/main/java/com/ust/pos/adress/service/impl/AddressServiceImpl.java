package com.ust.pos.adress.service.impl;

import com.ust.pos.adress.service.AddressService;
import com.ust.pos.common.CommonService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
    public static final String ADDRESS_WITH_IDENTIFIER = "Address with identifier ' ";
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        String identifier = addressDto.getIdentifier();
        Address existingAddress = addressRepository.findByIdentifier(identifier);
        if (existingAddress != null) {
            if (!existingAddress.isDeleted()) {
                addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " already exists");
                addressDto.setSuccess(false);
                return addressDto;
            }
            addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            addressDto.setSuccess(false);
            return addressDto;
        }
        Address address = modelMapper.map(addressDto, Address.class);
        setAuditFields(address, true);
        addressRepository.save(address);
        Address savedAddress = addressRepository.findByIdentifier(identifier);
        modelMapper.map(savedAddress, addressDto);
        addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " 'added Successfully");
        addressDto.setSuccess(true);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        String identifier = addressDto.getIdentifier();
        Address existingAddress = addressRepository.findByIdentifier(addressDto.getIdentifier());
        if (existingAddress == null) {
            addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " 'not found");
            addressDto.setSuccess(false);
            return addressDto;
        }
        modelMapper.map(addressDto, existingAddress);
        setAuditFields(existingAddress, false);
        addressRepository.save(existingAddress);
        Address updatedAddress = addressRepository.findByIdentifier(identifier);
        modelMapper.map(updatedAddress, addressDto);
        addressDto.setMessage(ADDRESS_WITH_IDENTIFIER + identifier + " 'Updated");
        addressDto.setSuccess(true);
        return addressDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNoAndDeletedFalse(phoneNo);
        if (addresses == null) return false;
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
        Page<Address> addressPage = addressRepository.findAll(pageable);
        return modelMapper.map(addressPage.getContent(), listType);
    }

    @Override
    public AddressDto findByIdentifier(String identifier) {
        Address address = addressRepository.findByIdentifier(identifier);
        if (address == null) {
            throw new ResourceNotFoundException("Address with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> findAllByPhoneNumber(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNoAndDeletedFalse(phoneNo), listType);
    }
}
