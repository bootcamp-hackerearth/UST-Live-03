package com.ust.pos.address.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.address.service.AddressService;
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

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository,
                              ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDto findByIdentifier(String identifier) {
        return modelMapper.map(addressRepository.findByIdentifier(identifier), AddressDto.class);
    }

    @Override
    public List<AddressDto> findAllByPhoneNo(String phoneNo) {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAllByPhoneNo(phoneNo), listType);
    }

    @Override
    public AddressDto save(AddressDto addressDto) {
        Address address = modelMapper.map(addressDto, Address.class);
        setAuditFields(address, true);
        Address saved = addressRepository.save(address);
        return modelMapper.map(saved, AddressDto.class);
    }

    @Override
    public AddressDto update(AddressDto addressDto) {
        String phoneNo = addressDto.getPhoneNo();

        Address existing = addressRepository.findByPhoneNoAndAddressType(
                phoneNo, addressDto.getAddressType()
        );

        if (existing == null) {
            existing = modelMapper.map(addressDto, Address.class);
            setAuditFields(existing, true);
        } else {
            Long id = existing.getId();
            modelMapper.map(addressDto, existing);
            existing.setId(id);
            setAuditFields(existing, false);
        }

        Address saved = addressRepository.save(existing);
        return modelMapper.map(saved, AddressDto.class);
    }

    @Override
    public boolean delete(String phoneNo) {
        List<Address> list = addressRepository.findAllByPhoneNo(phoneNo);
        for (Address address : list) {
            softDelete(address);
            setAuditFields(address, false);
        }
        addressRepository.saveAll(list);
        return true;
    }

    @Override
    public List<AddressDto> findAll() {
        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listType);
    }
}
