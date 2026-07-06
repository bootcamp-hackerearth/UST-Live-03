package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void findByIdentifierTest() {
        Address address = new Address();
        address.setIdentifier("ADDR01");
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01")).thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto response = addressService.findByIdentifier("ADDR01");

        Assertions.assertEquals("ADDR01", response.getIdentifier());
    }

    @Test
    void findAllByPhoneNoTest() {
        Address address = new Address();
        List<Address> addresses = List.of(address);
        AddressDto addressDto = new AddressDto();
        List<AddressDto> addressDtos = List.of(addressDto);

        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(addresses);
        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(addressDtos);

        List<AddressDto> response = addressService.findAllByPhoneNo("1234567890");

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveTestSuccess() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01")).thenReturn(null);
        Address address = new Address();
        Mockito.when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        Mockito.when(addressRepository.save(address)).thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertEquals("ADDR01", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR01");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01")).thenReturn(existingAddress);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Address with identifier - ADDR01 already exists", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR01");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01")).thenReturn(existingAddress);
        Mockito.when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertEquals("ADDR01", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01")).thenReturn(null);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Address with identifier - ADDR01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Address address1 = new Address();
        Address address2 = new Address();
        List<Address> addresses = new ArrayList<>(List.of(address1, address2));

        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(addresses);
        Mockito.when(addressRepository.saveAll(addresses)).thenReturn(addresses);

        boolean response = addressService.delete("1234567890");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailureNull() {
        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(null);

        boolean response = addressService.delete("1234567890");

        Assertions.assertFalse(response);
    }

    @Test
    void deleteTestFailureEmpty() {
        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(Collections.emptyList());

        boolean response = addressService.delete("1234567890");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Address address = new Address();
        List<Address> addresses = List.of(address);
        Page<Address> addressPage = new PageImpl<>(addresses, pageable, addresses.size());

        AddressDto addressDto = new AddressDto();
        List<AddressDto> addressDtos = List.of(addressDto);

        Mockito.when(addressRepository.findByDeletedFalse(pageable)).thenReturn(addressPage);
        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(addressDtos);

        List<AddressDto> response = addressService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }
}