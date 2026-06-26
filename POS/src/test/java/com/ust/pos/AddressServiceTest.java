package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private AddressServiceImpl addressService;

    private Address address;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        address = new Address();
        address.setIdentifier("ID1");

        addressDto = new AddressDto();
        addressDto.setIdentifier("ID1");
    }

    @Test
    void testFindAll() {
        List<Address> addressList = Collections.singletonList(address);
        List<AddressDto> dtoList = Collections.singletonList(addressDto);

        when(addressRepository.findAll()).thenReturn(addressList);
        when(modelMapper.map(eq(addressList), any(Type.class))).thenReturn(dtoList);

        List<AddressDto> result = addressService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSave_Success() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(null);
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);

        doNothing().when(addressService).setAuditFields(any(), eq(true));

        AddressDto result = addressService.save(addressDto);

        assertNotNull(result);
        verify(addressRepository).save(address);
    }

    @Test
    void testSave_AlreadyExists() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Address already exists"));
    }

    @Test
    void testDelete() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(address);

        doNothing().when(addressService).softDelete(address);
        doNothing().when(addressService).setAuditFields(address, false);

        addressService.delete("ID1");

        verify(addressRepository).save(address);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto result = addressService.findByIdentifier("ID1");

        assertNotNull(result);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(null);

        AddressDto result = addressService.findByIdentifier("ID1");

        assertNull(result);
    }

    @Test
    void testUpdate_Success() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(address);

        doNothing().when(modelMapper).map(addressDto, address);
        doNothing().when(addressService).setAuditFields(address, false);

        AddressDto result = addressService.update(addressDto);

        assertNotNull(result);
        verify(addressRepository).save(address);
    }

    @Test
    void testUpdate_NotFound() {
        when(addressRepository.findByIdentifier("ID1")).thenReturn(null);

        AddressDto result = addressService.update(addressDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Address not found"));
    }

    @Test
    void testFindAllByPhoneNumber() {
        List<Address> addressList = Collections.singletonList(address);
        List<AddressDto> dtoList = Collections.singletonList(addressDto);

        when(addressRepository.findAllByPhoneNo("9999999999")).thenReturn(addressList);
        when(modelMapper.map(eq(addressList), any(Type.class))).thenReturn(dtoList);

        List<AddressDto> result = addressService.findAllByPhoneNumber("9999999999");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}