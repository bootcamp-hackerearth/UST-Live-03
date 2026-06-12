package com.ust.pos;

import com.ust.pos.customer.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
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

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    private AddressDto buildDto() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("BILLING");
        dto.setSuccess(true);
        return dto;
    }

    @Test
    void testFindByPhoneNoAndAddressType() {

        Address address = new Address();
        AddressDto dto = buildDto();

        when(addressRepository.findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(address);

        when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);

        AddressDto result = addressService.findByPhoneNoAndAddressType(123L, "BILLING");
        assertNotNull(result);
        verify(addressRepository).findByPhoneNoAndAddressType(123L, "BILLING");
    }

    @Test
    void testSave() {

        AddressDto dto = buildDto();
        Address address = new Address();

        when(addressRepository.findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(null);

        when(modelMapper.map(dto, Address.class))
                .thenReturn(address);

        AddressDto result = addressService.save(dto);

        assertNotNull(result);

        verify(addressRepository).save(address);
    }

    @Test
    void testSave_Duplicate() {

        AddressDto dto = buildDto();

        when(addressRepository.findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(new Address());

        AddressDto result = addressService.save(dto);

        assertFalse(result.isSuccess());

        verify(addressRepository, never()).save(any());
    }

    @Test
    void testUpdate() {

        AddressDto dto = buildDto();
        Address existing = new Address();

        when(addressRepository.findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(existing);

        AddressDto result = addressService.update(dto);

        assertNotNull(result);

        verify(addressRepository).save(existing);
    }

    @Test
    void testUpdate_NotFound() {

        AddressDto dto = buildDto();

        when(addressRepository.findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(null);

        AddressDto result = addressService.update(dto);

        assertFalse(result.isSuccess());

        verify(addressRepository, never()).save(any());
    }

    @Test
    void testFindAll() {

        List<Address> addresses =
                List.of(new Address());

        List<AddressDto> dtos =
                List.of(new AddressDto());

        when(addressRepository.findAll())
                .thenReturn(addresses);

        when(modelMapper.map(eq(addresses), any(Type.class)))
                .thenReturn(dtos);

        List<AddressDto> result =
                addressService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteByPhone() {

        addressService.deleteByPhone(123L);

        verify(addressRepository)
                .deleteByPhoneNo(123L);
    }
}