package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class AddressServiceImpl extends BaseService implements AddressService {

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
        return modelMapper.map(addressRepository.findByIsDeletedFalse(), listType);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {

        Address existingAddress =
                addressRepository.findByPhoneNoAndAddressType(
                        addressDto.getPhoneNo(),
                        addressDto.getAddressType()
                );

        if (existingAddress != null) {

            if (existingAddress.isDeleted()) {
                addressDto.setMessage(
                        "Address - " + addressDto.getPhoneNo() + " (" + addressDto.getAddressType() +
                                ") already exists but was deleted, Please contact Administrator"
                );
                addressDto.setSuccess(false);
                return addressDto;
            }

            modelMapper.map(addressDto, existingAddress);
            setModifiedDetails(existingAddress);
            addressRepository.save(existingAddress);

        } else {
            Address newAddress = new Address();
            modelMapper.map(addressDto, newAddress);
            setCreatedDetails(newAddress);
            addressRepository.save(newAddress);
        }

        addressDto.setSuccess(true);
        return addressDto;
    }


    @Override
    public AddressDto update(AddressDto addressDto) {

        Address existingAddress = addressRepository.findByPhoneNoAndAddressType(addressDto.getPhoneNo(), addressDto.getAddressType());
        if (existingAddress == null) {
            addressDto.setMessage("Address with address - " + addressDto.getAddressType() + " not found");
            addressDto.setSuccess(false);
            return addressDto;
        }
        modelMapper.map(addressDto, existingAddress);
        setModifiedDetails(existingAddress);
        addressRepository.save(existingAddress);
        return addressDto;
    }

    @Override
    public AddressDto findByPhoneNoAndAddressType(String phoneNo, String addressType) {

        Address address = addressRepository.findByPhoneNoAndAddressType(phoneNo, addressType);

        if (address == null) {
            return new AddressDto();
        }

        return modelMapper.map(address, AddressDto.class);

    }

    @Override
    public void deleteByPhoneNo(String phoneNo) {
        List<Address> address = addressRepository.findByPhoneNo(phoneNo);
        addressRepository.deleteAll(address);
    }
}
