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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setId(1L);
        address.setIdentifier("ADDR-123");
        address.setPhoneNo("1234567890");
        address.setAddressType("HOME");

        addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR-123");
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("HOME");
    }

    @Test
    void testFindByIdentifier() {
        when(addressRepository.findByIdentifier("ADDR-123")).thenReturn(address);

        AddressDto result = addressService.findByIdentifier("ADDR-123");

        assertNotNull(result);
        assertEquals("ADDR-123", result.getIdentifier());
        verify(addressRepository, times(1)).findByIdentifier("ADDR-123");
    }

    @Test
    void testFindAllByPhoneNo() {
        List<Address> addressList = Collections.singletonList(address);
        when(addressRepository.findAllByPhoneNo("1234567890")).thenReturn(addressList);

        List<AddressDto> result = addressService.findAllByPhoneNo("1234567890");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1234567890", result.get(0).getPhoneNo());
        verify(addressRepository, times(1)).findAllByPhoneNo("1234567890");
    }

    @Test
    void testSave() {
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        assertNotNull(result);
        assertEquals("ADDR-123", result.getIdentifier());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testUpdate_WhenAddressDoesNotExist() {
        when(addressRepository.findByPhoneNoAndAddressType("1234567890", "HOME")).thenReturn(null);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressDto result = addressService.update(addressDto);

        assertNotNull(result);
        verify(addressRepository, times(1)).findByPhoneNoAndAddressType("1234567890", "HOME");
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testUpdate_WhenAddressExists() {
        Address existingAddress = new Address();
        existingAddress.setId(5L);
        existingAddress.setPhoneNo("1234567890");
        existingAddress.setAddressType("HOME");

        when(addressRepository.findByPhoneNoAndAddressType("1234567890", "HOME")).thenReturn(existingAddress);
        when(addressRepository.save(any(Address.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AddressDto result = addressService.update(addressDto);

        assertNotNull(result);
        verify(addressRepository, times(1)).findByPhoneNoAndAddressType("1234567890", "HOME");
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testDelete() {
        List<Address> addressList = Arrays.asList(address, new Address());
        when(addressRepository.findAllByPhoneNo("1234567890")).thenReturn(addressList);
        when(addressRepository.saveAll(anyList())).thenReturn(addressList);

        boolean result = addressService.delete("1234567890");

        assertTrue(result);
        verify(addressRepository, times(1)).findAllByPhoneNo("1234567890");
        verify(addressRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testFindAll() {
        List<Address> addressList = Collections.singletonList(address);
        when(addressRepository.findAll()).thenReturn(addressList);

        List<AddressDto> result = addressService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(addressRepository, times(1)).findAll();
    }
}