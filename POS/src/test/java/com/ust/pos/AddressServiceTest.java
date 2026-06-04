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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void testFindAll() {

        List<Address> addresses = Arrays.asList(new Address(), new Address());
        List<AddressDto> dtoList = Arrays.asList(new AddressDto(), new AddressDto());

        when(addressRepository.findAll()).thenReturn(addresses);
        when(modelMapper.map(eq(addresses), any(Type.class))).thenReturn(dtoList);

        List<AddressDto> result = addressService.findAll();

        assertEquals(2, result.size());
        verify(addressRepository).findAll();
    }

    @Test
    void testSaveExistingAddress() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("BILLING");

        Address existing = new Address();

        when(addressRepository.findByPhoneNoAndAddressType("123", "BILLING"))
                .thenReturn(existing);

        AddressDto result = addressService.save(dto);

        assertNotNull(result);
        verify(addressRepository).save(existing);
    }

    @Test
    void testSaveNewAddress() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("SHIPPING");

        when(addressRepository.findByPhoneNoAndAddressType("123", "SHIPPING"))
                .thenReturn(null);

        doNothing().when(modelMapper).map(eq(dto), any(Address.class));

        AddressDto result = addressService.save(dto);

        assertNotNull(result);

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void testUpdateSuccess() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("BILLING");

        Address existing = new Address();

        when(addressRepository.findByPhoneNoAndAddressType("123", "BILLING"))
                .thenReturn(existing);

        AddressDto result = addressService.update(dto);

        assertNotNull(result);
        verify(addressRepository).save(existing);
    }

    @Test
    void testUpdateNotFound() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("SHIPPING");

        when(addressRepository.findByPhoneNoAndAddressType("123", "SHIPPING"))
                .thenReturn(null);

        AddressDto result = addressService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testFindByPhoneAndTypeFound() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        when(addressRepository.findByPhoneNoAndAddressType("123", "BILLING"))
                .thenReturn(address);

        when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);

        AddressDto result = addressService.findByPhoneNoAndAddressType("123", "BILLING");

        assertNotNull(result);
    }

    @Test
    void testFindByPhoneAndTypeNotFound() {

        when(addressRepository.findByPhoneNoAndAddressType("123", "BILLING"))
                .thenReturn(null);

        AddressDto result = addressService.findByPhoneNoAndAddressType("123", "BILLING");

        assertNotNull(result);
    }

    @Test
    void testDeleteByPhoneNo() {

        addressService.deleteByPhoneNo("123");

        verify(addressRepository).deleteByPhoneNo("123");
    }
}