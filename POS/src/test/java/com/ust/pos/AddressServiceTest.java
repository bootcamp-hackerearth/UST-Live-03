package com.ust.pos;

import com.ust.pos.customer.service.impl.AddressServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void findByPhoneNoAndAddressType_Found() {
        Address address = new Address();
        address.setPhoneNo(9876543210L);
        address.setAddressType("billingAddress");

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(9876543210L, "billingAddress"))
                .thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);

        AddressDto result = addressService.findByPhoneNoAndAddressType(
                9876543210L, "billingAddress");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("billingAddress", result.getAddressType());
    }

    @Test
    void findByPhoneNoAndAddressType_NotFound() {
        when(addressRepository.findByPhoneNoAndAddressType(9876543210L, "billingAddress"))
                .thenReturn(null);

        AddressDto result = addressService.findByPhoneNoAndAddressType(
                9876543210L, "billingAddress");

        Assertions.assertNotNull(result);
    }

    @Test
    void save_NewAddress() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        Address address = new Address();

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L, "billingAddress")).thenReturn(null);
        when(modelMapper.map(dto, Address.class)).thenReturn(address);

        AddressDto result = addressService.save(dto);

        Assertions.assertEquals("billingAddress", result.getAddressType());
        verify(addressRepository).save(address);
    }

    @Test
    void save_AddressAlreadyExists() {
        Address existing = new Address();

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L, "billingAddress")).thenReturn(existing);

        AddressDto result = addressService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void update_AddressExists() {
        Address existing = new Address();

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L, "billingAddress")).thenReturn(existing);

        AddressDto result = addressService.update(dto);

        Assertions.assertEquals("billingAddress", result.getAddressType());
        verify(addressRepository).save(existing);
    }

    @Test
    void update_AddressNotFound() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L, "billingAddress")).thenReturn(null);

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void findAllTest() {
        List<Address> addresses = List.of(new Address(), new Address());
        List<AddressDto> dtoList = List.of(new AddressDto(), new AddressDto());

        when(addressRepository.findAll()).thenReturn(addresses);
        when(modelMapper.map(
                Mockito.eq(addresses),
                Mockito.any(java.lang.reflect.Type.class))
        ).thenReturn(dtoList);

        List<AddressDto> result = addressService.findAll();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void deleteByPhoneNoTest() {
        doNothing().when(addressRepository).deleteByPhoneNo(9876543210L);

        addressService.deleteByPhoneNo(9876543210L);

        verify(addressRepository).deleteByPhoneNo(9876543210L);
    }
}