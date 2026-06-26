package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.modell.Address;
import com.ust.pos.modell.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void findByIdentifier_shouldReturnDto() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        when(addressRepository.findByIdentifier("ADDR01")).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);
        AddressDto result = addressService.findByIdentifier("ADDR01");
        assertNotNull(result);
        verify(addressRepository).findByIdentifier("ADDR01");
        verify(modelMapper).map(address, AddressDto.class);
    }

    @Test
    void findAllByPhoneNo_shouldReturnMappedList() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(List.of(address));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(dto));
        List<AddressDto> result = addressService.findAllByPhoneNo("9876543210");
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findAllByPhoneNo_shouldReturnEmptyList_whenNoData() {
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(new ArrayList<>());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(new ArrayList<>());
        List<AddressDto> result = addressService.findAllByPhoneNo("9876543210");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(addressRepository).findAllByPhoneNoAndDeletedFalse("9876543210");
        verify(modelMapper).map(anyList(), any(Type.class));
    }

    @Test
    void findAllByPhoneNo_shouldReturnNull_whenRepositoryReturnsNull() {
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(null);
        List<AddressDto> result = addressService.findAllByPhoneNo("9876543210");
        assertNull(result);
    }

    @Test
    void save_shouldPersistAddress() {
        AddressDto dto = new AddressDto();
        Address entity = new Address();
        when(modelMapper.map(dto, Address.class)).thenReturn(entity);
        when(addressRepository.save(entity)).thenReturn(entity);
        AddressDto result = addressService.save(dto);
        assertNotNull(result);
        verify(addressRepository).save(entity);
    }

    @Test
    void update_shouldUpdateAddress_whenExists() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("9999999999");
        dto.setAddressType("Billing");
        Address existing = new Address();
        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse("9999999999", "Billing")).thenReturn(existing);
        AddressDto result = addressService.update(dto);
        assertNotNull(result);
        verify(modelMapper).map(dto, existing);
        verify(addressRepository).save(existing);
    }

    @Test
    void update_shouldReturnNull_whenNotFound() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("111");
        dto.setAddressType("Billing");
        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse("111", "Billing")).thenReturn(null);
        AddressDto result = addressService.update(dto);
        assertNull(result);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void delete_shouldSoftDeleteAddresses() {
        Address address = new Address();
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(List.of(address));
        boolean result = addressService.delete("9876543210");
        assertTrue(result);
        assertTrue(address.getDeleted());
        verify(addressRepository).saveAll(anyList());
    }

    @Test
    void delete_shouldHandleEmptyList() {
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(new ArrayList<>());
        boolean result = addressService.delete("9876543210");
        assertTrue(result);
        verify(addressRepository).saveAll(anyList());
    }

    @Test
    void delete_shouldHandleNullList() {
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("9876543210")).thenReturn(null);
        boolean result = addressService.delete("9876543210");
        assertTrue(result);
        verify(addressRepository, never()).saveAll(anyList());
    }

    @Test
    void findAll_shouldReturnAllAddresses() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        when(addressRepository.findAll()).thenReturn(List.of(address));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(dto));
        List<AddressDto> result = addressService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}