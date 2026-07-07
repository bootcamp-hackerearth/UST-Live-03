package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccess() {

        Address address = new Address();
        address.setIdentifier("ADDR1");

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR1");

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(address);

        when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);

        AddressDto result = addressService.findByIdentifier("ADDR1");

        assertNotNull(result);
        assertEquals("ADDR1", result.getIdentifier());

        verify(addressRepository).findByIdentifier("ADDR1");

        verify(modelMapper).map(address, AddressDto.class);
    }

    @Test
    void findByIdentifierNotFound() {

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> addressService.findByIdentifier("ADDR1"));

        verify(addressRepository).findByIdentifier("ADDR1");
    }

    @Test
    void findAllByPhoneNoTest() {

        List<Address> addresses = List.of(new Address());

        List<AddressDto> dtos = List.of(new AddressDto());

        when(addressRepository.findAllByPhoneNo("9999999999")).thenReturn(addresses);

        when(modelMapper.map(eq(addresses), any(Type.class))).thenReturn(dtos);

        List<AddressDto> result = addressService.findAllByPhoneNo("9999999999");

        assertEquals(1, result.size());

        verify(addressRepository).findAllByPhoneNo("9999999999");
    }

    @Test
    void saveSuccess() {

        AddressDto dto = new AddressDto();

        dto.setIdentifier("ADDR1");

        Address address = new Address();

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        when(modelMapper.map(dto, Address.class)).thenReturn(address);

        AddressDto result = addressService.save(dto);

        assertEquals("ADDR1", result.getIdentifier());

        verify(addressRepository).save(address);
    }

    @Test
    void saveAlreadyExists() {

        AddressDto dto = new AddressDto();

        dto.setIdentifier("ADDR1");

        Address existing = new Address();

        existing.setDeleted(false);

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existing);

        AddressDto result = addressService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Address with identifier - ADDR1 already exists", result.getMessage());

        verify(addressRepository, never()).save(any());
    }

    @Test
    void saveSoftDeleted() {

        AddressDto dto = new AddressDto();

        dto.setIdentifier("ADDR1");

        Address existing = new Address();

        existing.setDeleted(true);

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existing);

        AddressDto result = addressService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Address with identifier - ADDR1 has been soft deleted.(Rollback by changing status", result.getMessage());

        verify(addressRepository, never()).save(any());
    }

    @Test
    void updateSuccess() {

        AddressDto dto = new AddressDto();

        dto.setIdentifier("ADDR1");

        Address existing = new Address();

        existing.setIdentifier("ADDR1");

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existing);

        doNothing().when(modelMapper).map(dto, existing);

        AddressDto result = addressService.update(dto);

        assertEquals("ADDR1", result.getIdentifier());

        verify(modelMapper).map(dto, existing);

        verify(addressRepository).save(existing);
    }

    @Test
    void updateNotFound() {

        AddressDto dto = new AddressDto();

        dto.setIdentifier("ADDR1");

        when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> addressService.update(dto));

        verify(addressRepository).findByIdentifier("ADDR1");
    }

    @Test
    void deleteSuccess() {

        Address address = new Address();

        List<Address> list = List.of(address);

        when(addressRepository.findAllByPhoneNo("9999999999")).thenReturn(list);

        boolean result = addressService.delete("9999999999");

        assertTrue(result);

        assertTrue(address.isDeleted());

        verify(addressRepository).save(address);
    }

    @Test
    void deleteEmptyList() {

        when(addressRepository.findAllByPhoneNo("9999999999")).thenReturn(Collections.emptyList());

        boolean result = addressService.delete("9999999999");

        assertTrue(result);

        verify(addressRepository, never()).save(any());
    }

    @Test
    void findAllTest() {

        List<Address> addresses = List.of(new Address());

        List<AddressDto> dtos = List.of(new AddressDto());

        when(addressRepository.findAll()).thenReturn(addresses);

        when(modelMapper.map(eq(addresses), any(Type.class))).thenReturn(dtos);

        List<AddressDto> result = addressService.findAll();

        assertEquals(1, result.size());

        verify(addressRepository).findAll();
    }
}