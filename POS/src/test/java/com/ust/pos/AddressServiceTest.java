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
import java.util.Arrays;
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
    void findByPhoneNoAndAddressTypeSuccessTest() {

        Address address = new Address();
        address.setAddressType("billingAddress");
        address.setPhoneNo(9999999999L);

        AddressDto dto = new AddressDto();
        dto.setAddressType("billingAddress");
        dto.setPhoneNo(9999999999L);

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "billingAddress")).thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);
        AddressDto result = addressService.findByPhoneNoAndAddressType(9999999999L, "billingAddress");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("billingAddress", result.getAddressType());
    }

    @Test
    void saveSuccessTest() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(8888888888L);
        dto.setAddressType("shippingAddress");

        Address address = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(8888888888L, "shippingAddress")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Address.class)).thenReturn(address);

        AddressDto result = addressService.save(dto);

        Assertions.assertEquals("shippingAddress", result.getAddressType());

        verify(addressRepository).save(address);
    }

    @Test
    void saveFailureAlreadyExistsTest() {

        Address existing = new Address();

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(8888888888L);
        dto.setAddressType("shippingAddress");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(8888888888L, "shippingAddress")).thenReturn(existing);

        AddressDto result = addressService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));

        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Address existing = new Address();
        existing.setPhoneNo(7777777777L);
        existing.setAddressType("billingAddress");

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(7777777777L);
        dto.setAddressType("billingAddress");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(7777777777L, "billingAddress")).thenReturn(existing);

        AddressDto result = addressService.update(dto);

        verify(modelMapper).map(dto, existing);
        verify(addressRepository).save(existing);

        Assertions.assertEquals("billingAddress", result.getAddressType());
    }

    @Test
    void updateFailureNotFoundTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(7777777777L);
        dto.setAddressType("billingAddress");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(7777777777L, "billingAddress")).thenReturn(null);

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));

        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {

        List<Address> addresses = Arrays.asList(new Address(), new Address());
        List<AddressDto> dtoList = Arrays.asList(new AddressDto(), new AddressDto());

        Mockito.when(addressRepository.findAll()).thenReturn(addresses);
        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(Type.class))).thenReturn(dtoList);

        List<AddressDto> result = addressService.findAll();

        Assertions.assertEquals(2, result.size());

        verify(addressRepository).findAll();
    }

    @Test
    void findByPhoneNoAndAddressTypeNullPhoneTest() {
        AddressDto result = addressService.findByPhoneNoAndAddressType(null, "billingAddress");

        Assertions.assertNotNull(result);
    }

    @Test
    void findByPhoneNoAndAddressTypeZeroPhoneTest() {
        AddressDto result = addressService.findByPhoneNoAndAddressType(0L, "billingAddress");
        Assertions.assertNotNull(result);
    }

    @Test
    void findByPhoneNoAndAddressTypeNotFoundTest() {
        Mockito.when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "billingAddress")).thenReturn(null);

        AddressDto result = addressService.findByPhoneNoAndAddressType(9999999999L, "billingAddress");

        Assertions.assertNotNull(result);
    }

    @Test
    void saveNullPhoneTest() {
        AddressDto dto = new AddressDto();

        AddressDto result = addressService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Phone number is required to save address", result.getMessage());
    }

    @Test
    void saveZeroPhoneTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(0L);

        AddressDto result = addressService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Phone number is required to save address", result.getMessage());
    }

    @Test
    void updateNullPhoneTest() {
        AddressDto dto = new AddressDto();

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Phone number is required to update address", result.getMessage());
    }

    @Test
    void updateZeroPhoneTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(0L);

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Phone number is required to update address", result.getMessage());
    }
}