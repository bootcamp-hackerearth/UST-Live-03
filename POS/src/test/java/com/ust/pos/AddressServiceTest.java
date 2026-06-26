package com.ust.pos;

import com.ust.pos.customer.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl service;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        when(addressRepository.findByIsDeletedFalse()).thenReturn(List.of(new Address()));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new AddressDto()));

        List<AddressDto> result = service.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void saveNewTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("HOME");

        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(null);

        AddressDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(addressRepository).save(any());
    }

    @Test
    void saveExistingUpdateTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("HOME");

        Address existing = new Address();
        existing.setDeleted(false);

        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(existing);

        AddressDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(addressRepository).save(existing);
    }

    @Test
    void saveExistingDeletedTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("HOME");

        Address existing = new Address();
        existing.setDeleted(true);

        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(existing);

        AddressDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("HOME");

        Address existing = new Address();

        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(existing);

        AddressDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(addressRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("HOME");

        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(null);

        AddressDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(addressRepository, never()).save(any());
    }

    @Test
    void findByPhoneNoAndAddressTypeFoundTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);

        AddressDto result = service.findByPhoneNoAndAddressType("123", "HOME");

        assertNotNull(result);
    }

    @Test
    void findByPhoneNoAndAddressTypeNotFoundTest() {
        when(addressRepository.findByPhoneNoAndAddressType("123", "HOME")).thenReturn(null);

        AddressDto result = service.findByPhoneNoAndAddressType("123", "HOME");

        assertNotNull(result);
    }

    @Test
    void deleteByPhoneNoTest() {
        Address address = new Address();
        address.setDeleted(false);

        when(addressRepository.findByPhoneNo("123")).thenReturn(address);

        service.deleteByPhoneNo("123");

        assertTrue(address.isDeleted());
    }
}