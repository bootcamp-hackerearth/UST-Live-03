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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {

        Address address = new Address();
        address.setIdentifier("ADDR1");

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto response = addressService.findByIdentifier("ADDR1");

        Assertions.assertEquals("ADDR1", response.getIdentifier());

        Mockito.verify(addressRepository).findByIdentifier("ADDR1");
    }

    @Test
    void findAllByPhoneNoTest() {

        Address address = new Address();
        AddressDto addressDto = new AddressDto();

        List<Address> addresses = List.of(address);
        List<AddressDto> addressDtos = List.of(addressDto);

        Mockito.when(addressRepository.findAllByPhoneNo("9999999999")).thenReturn(addresses);

        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(addressDtos);

        List<AddressDto> response = addressService.findAllByPhoneNo("9999999999");

        Assertions.assertEquals(1, response.size());

        Mockito.verify(addressRepository).findAllByPhoneNo("9999999999");
    }

    @Test
    void saveTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address address = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        Mockito.when(modelMapper.map(addressDto, Address.class)).thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Mockito.verify(addressRepository).save(address);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address existingAddress = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existingAddress);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Address with identifier - ADDR1 already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existingAddress);

        Mockito.when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        Mockito.doNothing().when(modelMapper).map(addressDto, existingAddress);

        AddressDto response = addressService.update(addressDto);

        Mockito.verify(addressRepository).save(existingAddress);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Address with identifier - ADDR1 not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Address address = new Address();

        List<Address> addresses = List.of(address);

        Mockito.when(addressRepository.findAllByPhoneNo("9999999999")).thenReturn(addresses);

        Mockito.doNothing().when(addressRepository).deleteAll(addresses);

        boolean result = addressService.delete("9999999999");

        Assertions.assertTrue(result);

        Mockito.verify(addressRepository).findAllByPhoneNo("9999999999");

        Mockito.verify(addressRepository).deleteAll(addresses);
    }

    @Test
    void findAllTest() {

        Address address = new Address();
        AddressDto addressDto = new AddressDto();

        List<Address> addresses = List.of(address);
        List<AddressDto> addressDtos = List.of(addressDto);

        Mockito.when(addressRepository.findAll()).thenReturn(addresses);

        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(java.lang.reflect.Type.class))).thenReturn(addressDtos);

        List<AddressDto> response = addressService.findAll();

        Assertions.assertEquals(1, response.size());

        Mockito.verify(addressRepository).findAll();
    }
}