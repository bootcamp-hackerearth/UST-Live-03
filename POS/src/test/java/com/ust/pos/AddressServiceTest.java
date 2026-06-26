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
        address.setIdentifier("Admin");

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("Admin");

        Mockito.when(addressRepository.findByIdentifier("Admin")).thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto response = addressService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findAllByPhoneNoTest() {
        Address address = new Address();
        address.setIdentifier("Admin");

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("Admin");

        List<Address> addresses = List.of(address);
        List<AddressDto> addressDtos = List.of(addressDto);

        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(addresses);
        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(addressDtos);

        List<AddressDto> response = addressService.findAllByPhoneNo("9876543210");

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void saveTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("Admin");

        Mockito.when(addressRepository.findByIdentifier("Admin")).thenReturn(null);
        Address address = new Address();
        Mockito.when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        Mockito.when(addressRepository.save(address)).thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("Admin");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("Admin");

        Mockito.when(addressRepository.findByIdentifier("Admin")).thenReturn(existingAddress);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("Admin");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("Admin");

        Mockito.when(addressRepository.findByIdentifier("Admin")).thenReturn(existingAddress);
        Mockito.when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("Admin");

        Mockito.when(addressRepository.findByIdentifier("Admin")).thenReturn(null);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Address address = new Address();
        address.setIdentifier("Admin");
        List<Address> addresses = List.of(address);

        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(addresses);
        Mockito.when(addressRepository.saveAll(addresses)).thenReturn(addresses);

        boolean response = addressService.delete("9876543210");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailureNull() {
        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(null);

        boolean response = addressService.delete("9876543210");

        Assertions.assertFalse(response);
    }

    @Test
    void deleteTestFailureEmpty() {
        Mockito.when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(new ArrayList<>());

        boolean response = addressService.delete("9876543210");

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