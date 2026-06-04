package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.modell.Address;
import com.ust.pos.modell.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository repository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private AddressServiceImpl service;

    private Address address;
    private AddressDto dto;

    @BeforeEach
    void setup() {
        address = new Address();
        address.setPhoneNo("9876543210");
        address.setAddressType("HOME");
        dto = new AddressDto();
        dto.setPhoneNo("9876543210");
        dto.setAddressType("HOME");
    }

    @Test
    void findByIdentifier_shouldHandleFoundAndNotFound() {
        when(repository.findByIdentifier("ID1")).thenReturn(address);
        when(mapper.map(address, AddressDto.class)).thenReturn(dto);
        AddressDto result = service.findByIdentifier("ID1");
        assertNotNull(result);
        when(repository.findByIdentifier("ID2")).thenReturn(null);
        when(mapper.map(null, AddressDto.class)).thenReturn(null);
        assertNull(service.findByIdentifier("ID2"));
        verify(repository, times(2)).findByIdentifier(anyString());
    }

    @Test
    void findAllByPhoneNo_shouldHandleDataAndEmpty() {
        Type type = new TypeToken<List<AddressDto>>() {}.getType();
        when(repository.findAllByPhoneNo("987"))
                .thenReturn(List.of(address));
        when(mapper.map(any(), eq(type)))
                .thenReturn(List.of(dto));
        List<AddressDto> result = service.findAllByPhoneNo("987");
        assertEquals(1, result.size());
        when(repository.findAllByPhoneNo("000"))
                .thenReturn(List.of());
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAllByPhoneNo("000").isEmpty());
    }

    @Test
    void save_shouldMapAndPersist() {
        when(mapper.map(dto, Address.class)).thenReturn(address);
        AddressDto result = service.save(dto);
        verify(mapper).map(dto, Address.class);
        verify(repository).save(address);
        assertEquals(dto, result);
    }

    @Test
    void update_shouldFetchMapAndSave() {
        when(repository.findByPhoneNoAndAddressType("9876543210", "HOME"))
                .thenReturn(address);
        AddressDto result = service.update(dto);
        verify(repository).findByPhoneNoAndAddressType("9876543210", "HOME");
        verify(mapper).map(dto, address);
        verify(repository).save(address);
        assertEquals("9876543210", result.getPhoneNo());
    }

    @Test
    void delete_shouldHandleDataAndEmptyList() {
        when(repository.findAllByPhoneNo("987"))
                .thenReturn(List.of(address));
        assertTrue(service.delete("987"));
        verify(repository).deleteAll(anyList());
        when(repository.findAllByPhoneNo("000"))
                .thenReturn(List.of());
        assertTrue(service.delete("000"));
        verify(repository).deleteAll(List.of());
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Type type = new TypeToken<List<AddressDto>>() {}.getType();
        when(repository.findAll()).thenReturn(List.of(address));
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll().size());
        when(repository.findAll()).thenReturn(List.of());
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll().isEmpty());
    }
}