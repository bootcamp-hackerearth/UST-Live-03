package com.ust.pos.customer.service.impl;

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
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    @Override
    public AddressDto findByPhoneNoAndAddressType(Long phoneNo, String addressType) {
        Address address = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType);
        if (address == null) {
            return null;
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(
                addressDto.getPhoneNo(), addressDto.getAddressType());

        if (existingAddress != null) {
            if (Boolean.TRUE.equals(existingAddress.getIsDeleted())) {
                existingAddress.setIsDeleted(false);
                existingAddress.setAddressLine(addressDto.getAddressLine());
                existingAddress.setCity(addressDto.getCity());
                existingAddress.setState(addressDto.getState());
                existingAddress.setZipcode(addressDto.getZipcode());
                existingAddress.setCountry(addressDto.getCountry());
                addressRepository.save(existingAddress);
                return addressDto;
            }
            addressDto.setMessage("Address with phone number - " + addressDto.getPhoneNo() + " already exists");
            addressDto.setSuccess(false);
            return addressDto;
        }

        Address address = modelMapper.map(addressDto, Address.class);
        address.setIsDeleted(false);
        addressRepository.save(address);
        return addressDto;
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(
                addressDto.getPhoneNo(), addressDto.getAddressType());

        if (existingAddress == null) {
            addressDto.setMessage("Address with identifier - " + addressDto.getAddressType() + " not found");
            addressDto.setSuccess(false);
            return addressDto;
        }

        modelMapper.map(addressDto, existingAddress);
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
    public void softDeleteByPhone(Long phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNo(phoneNo);
        addresses.forEach(address -> {
            address.setIsDeleted(true);
            address.setStatus(false);
        });
        addressRepository.saveAll(addresses);
    }

    @Override
    public void restoreByPhone(Long phoneNo) {
        List<Address> addresses = addressRepository.findAllByPhoneNo(phoneNo);
        addresses.forEach(address -> address.setIsDeleted(false));
        addressRepository.saveAll(addresses);
    }
}