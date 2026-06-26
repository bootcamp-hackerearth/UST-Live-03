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

    private Address buildEntity() {
        Address entity = new Address();
        entity.setId(1L);
        entity.setPhoneNo(123L);
        entity.setAddressType("BILLING");
        entity.setDeleted(false);
        return entity;
    }

    @Test
    void shouldReturnAddressDto_whenAddressExists() {
        Address entity = buildEntity();
        AddressDto dto = buildDto();

        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING")).thenReturn(entity);
        when(modelMapper.map(entity, AddressDto.class)).thenReturn(dto);

        AddressDto result = addressService.findByPhoneNoAndAddressType(123L, "BILLING");

        assertNotNull(result);
        assertEquals("BILLING", result.getAddressType());

        verify(addressRepository).findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING");
        verify(modelMapper).map(entity, AddressDto.class);
    }

    @Test
    void shouldReturnNull_whenAddressNotFound() {
        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING")).thenReturn(null);

        AddressDto result = addressService.findByPhoneNoAndAddressType(123L, "BILLING");

        assertNull(result);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void shouldSaveAddress_whenNoDuplicateExists() {
        AddressDto dto = buildDto();
        Address entity = buildEntity();

        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING")).thenReturn(null);
        when(modelMapper.map(dto, Address.class)).thenReturn(entity);

        AddressDto result = addressService.save(dto);

        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(addressRepository).save(entity);
    }

    @Test
    void shouldReturnError_whenDuplicateAddressExists() {
        AddressDto dto = buildDto();

        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING")).thenReturn(buildEntity());

        AddressDto result = addressService.save(dto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));

        verify(addressRepository, never()).save(any());
    }

    @Test
    void shouldUpdateAddress_whenAddressExists() {
        AddressDto dto = buildDto();
        Address existing = buildEntity();

        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING")).thenReturn(existing);

        doAnswer(invocation -> {
            AddressDto source = invocation.getArgument(0);
            Address target = invocation.getArgument(1);
            target.setPhoneNo(source.getPhoneNo());
            target.setAddressType(source.getAddressType());
            return null;
        }).when(modelMapper).map(any(AddressDto.class), any(Address.class));

        AddressDto result = addressService.update(dto);

        assertNotNull(result);
        assertTrue(result.isSuccess());

        verify(addressRepository).save(existing);
    }

    @Test
    void shouldReturnError_whenUpdatingNonExistingAddress() {
        AddressDto dto = buildDto();

        when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse(123L, "BILLING")).thenReturn(null);

        AddressDto result = addressService.update(dto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));

        verify(addressRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllAddresses() {
        List<Address> entities = List.of(buildEntity());
        List<AddressDto> dtos = List.of(buildDto());

        when(addressRepository.findAll()).thenReturn(entities);
        when(modelMapper.map(eq(entities), any(Type.class))).thenReturn(dtos);

        List<AddressDto> result = addressService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(addressRepository).findAll();
    }

    @Test
    void shouldSoftDeleteAllAddressesByPhone() {
        Long phoneNo = 9876543210L;

        Address address1 = new Address();
        address1.setId(1L);
        address1.setPhoneNo(phoneNo);
        address1.setDeleted(false);

        Address address2 = new Address();
        address2.setId(2L);
        address2.setPhoneNo(phoneNo);
        address2.setDeleted(false);

        List<Address> addresses = Arrays.asList(address1, address2);

        when(addressRepository.findByPhoneNoAndDeletedFalse(phoneNo)).thenReturn(addresses);
        when(addressRepository.saveAll(anyList())).thenReturn(addresses);

        assertDoesNotThrow(() -> addressService.deleteByPhone(phoneNo));

        verify(addressRepository).findByPhoneNoAndDeletedFalse(phoneNo);
        verify(addressRepository).saveAll(addresses);

        verify(addressRepository, never()).deleteAll(anyList());
        verify(addressRepository, never()).delete(any());
    }
}
