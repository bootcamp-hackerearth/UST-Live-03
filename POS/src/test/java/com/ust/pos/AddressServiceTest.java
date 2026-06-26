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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address address = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);
        Mockito.when(modelMapper.map(addressDto, Address.class)).thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
        verify(addressRepository).save(address);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address existingAddress = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existingAddress);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Address with identifier - ADDR1 already exists", response.getMessage());
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existingAddress);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
        verify(modelMapper).map(addressDto, existingAddress);
        verify(addressRepository).save(existingAddress);
    }

    @Test
    void updateFailureTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Address with identifier - ADDR1 not found", response.getMessage());
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Address a1 = new Address();
        Address a2 = new Address();
        List<Address> addressList = List.of(a1, a2);

        Mockito.when(addressRepository.findByPhoneNo("1234567890")).thenReturn(addressList);

        addressService.delete("1234567890");

        verify(addressRepository).findByPhoneNo("1234567890");
        verify(addressRepository).deleteAll(addressList);
    }

    @Test
    void findAllSuccessTest() {
        Address a1 = new Address();
        List<Address> addressList = List.of(a1);

        AddressDto d1 = new AddressDto();
        List<AddressDto> addressDtos = List.of(d1);

        Mockito.when(addressRepository.findAll()).thenReturn(addressList);
        Mockito.when(modelMapper.map(Mockito.eq(addressList), Mockito.any(Type.class))).thenReturn(addressDtos);

        List<AddressDto> result = addressService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Address address = new Address();
        address.setIdentifier("ADDR1");

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto response = addressService.findByIdentifier("ADDR1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADDR1", response.getIdentifier());
    }

    @Test
    void findByPhoneNoSuccessTest() {
        Address a1 = new Address();
        List<Address> addressList = List.of(a1);

        AddressDto d1 = new AddressDto();
        List<AddressDto> addressDtos = List.of(d1);

        Mockito.when(addressRepository.findByPhoneNo("1234567890")).thenReturn(addressList);
        Mockito.when(modelMapper.map(Mockito.eq(addressList), Mockito.any(Type.class))).thenReturn(addressDtos);

        List<AddressDto> result = addressService.findByPhoneNo("1234567890");

        Assertions.assertEquals(1, result.size());
    }
}