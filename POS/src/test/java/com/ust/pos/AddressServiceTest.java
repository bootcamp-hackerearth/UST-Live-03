package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.models.Address;
import com.ust.pos.models.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void findByIdentifierTest() {
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
    void findAllByPhoneNoTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("111")).thenReturn(List.of(address));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(dto));
        List<AddressDto> result = addressService.findAllByPhoneNo("111");
        assertNotNull(result);
        assertEquals(1, result.size());
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("222")).thenReturn(new ArrayList<>());
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(new ArrayList<>());
        result = addressService.findAllByPhoneNo("222");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("333")).thenReturn(null);
        result = addressService.findAllByPhoneNo("333");
        assertNull(result);
    }

    @Test
    void saveTest() {
        AddressDto dto = new AddressDto();
        Address entity = new Address();
        when(modelMapper.map(dto, Address.class)).thenReturn(entity);
        when(addressRepository.save(entity)).thenReturn(entity);
        AddressDto result = addressService.save(dto);
        assertNotNull(result);
        verify(addressRepository).save(entity);
    }

    @Test
    void updateTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("9999999999");
        dto.setAddressType("Billing");
        Address existing = new Address();
        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse("9999999999", "Billing")).thenReturn(existing);
        AddressDto result = addressService.update(dto);
        assertNotNull(result);
        verify(modelMapper).map(dto, existing);
        verify(addressRepository).save(existing);
        AddressDto dto2 = new AddressDto();
        dto2.setPhoneNo("111");
        dto2.setAddressType("Billing");
        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse("111", "Billing")).thenReturn(null);
        result = addressService.update(dto2);
        assertNull(result);
    }

    @Test
    void deleteTest() {
        Address address = new Address();
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("111")).thenReturn(List.of(address));
        assertTrue(addressService.delete("111"));
        assertTrue(address.getDeleted());
        verify(addressRepository).saveAll(anyList());
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("222")).thenReturn(new ArrayList<>());
        assertTrue(addressService.delete("222"));
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("333")).thenReturn(null);
        assertTrue(addressService.delete("333"));
    }

    @Test
    void findAllTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        when(addressRepository.findAll()).thenReturn(List.of(address));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(dto));
        List<AddressDto> result = addressService.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}