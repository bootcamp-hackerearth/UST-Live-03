package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setPhoneNo("9876543210");
        address.setAddressType("HOME");

        addressDto = new AddressDto();
        addressDto.setPhoneNo("9876543210");
        addressDto.setAddressType("HOME");
    }


    @Test
    void findByIdentifier() {
        when(addressRepository.findByIdentifier("ADDR01")).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto result = addressService.findByIdentifier("ADDR01");

        assertNotNull(result);
        assertEquals("9876543210", result.getPhoneNo());
    }


    @Test
    void findAllByPhoneNo() {
        when(addressRepository.findAllByPhoneNo("9876543210")).thenReturn(List.of(address));

        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(addressDto));

        List<AddressDto> result = addressService.findAllByPhoneNo("9876543210");

        assertEquals(1, result.size());
        assertEquals("HOME", result.get(0).getAddressType());
    }


    @Test
    void save() {
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        verify(addressRepository).save(address);
        assertNotNull(result);
    }


    @Test
    void update() {
        when(addressRepository.findByPhoneNoAndAddressType("9876543210", "HOME")).thenReturn(address);

        AddressDto result = addressService.update(addressDto);

        verify(modelMapper).map(addressDto, address);
        verify(addressRepository).save(address);
        assertEquals("9876543210", result.getPhoneNo());
    }


    @Test
    void delete() {
        when(addressRepository.findAllByPhoneNo("9876543210")).thenReturn(List.of(address));

        boolean result = addressService.delete("9876543210");

        verify(addressRepository).deleteAll(anyList());
        assertTrue(result);
    }


    @Test
    void findAllAddresses() {
        when(addressRepository.findAll()).thenReturn(List.of(address));

        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(addressDto));

        List<AddressDto> result = addressService.findAll();

        assertEquals(1, result.size());
        assertEquals("HOME", result.get(0).getAddressType());
    }
}