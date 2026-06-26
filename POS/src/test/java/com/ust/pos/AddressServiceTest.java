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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void testFindByPhoneNoAndAddressType_Success() {
        Long phoneNumber = 9876543210L;
        String addressType = "Billing";

        Address address = new Address();
        address.setPhoneNumber(phoneNumber);
        address.setAddressType(addressType);

        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNumber(phoneNumber);
        addressDto.setAddressType(addressType);

        when(addressRepository.findByPhoneNumberAndAddressTypeAndDeletedFalse(phoneNumber, addressType))
                .thenReturn(address);
        when(modelMapper.map(address, AddressDto.class))
                .thenReturn(addressDto);

        AddressDto result = addressService.findByPhoneNoAndAddressType(phoneNumber, addressType);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertEquals(addressType, result.getAddressType());
    }

    @Test
    void testFindByPhoneNoAndAddressType_NotFound() {
        Long phoneNumber = 9876543210L;
        String addressType = "Billing";

        when(addressRepository.findByPhoneNumberAndAddressTypeAndDeletedFalse(phoneNumber, addressType))
                .thenReturn(null);

        AddressDto result = addressService.findByPhoneNoAndAddressType(phoneNumber, addressType);

        assertNull(result);
    }

    @Test
    void testSave_Success() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNumber(9876543210L);
        addressDto.setAddressType("Billing");

        Address address = new Address();
        address.setPhoneNumber(9876543210L);
        address.setAddressType("Billing");

        when(modelMapper.map(addressDto, Address.class))
                .thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        assertTrue(result.isSuccess());
        assertEquals("Address saved successfully", result.getMessage());

        verify(addressRepository).save(address);
    }

    @Test
    void testUpdate_ExistingAddress_Success() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNumber(9876543210L);
        addressDto.setAddressType("Billing");

        Address existingAddress = new Address();
        existingAddress.setPhoneNumber(9876543210L);
        existingAddress.setAddressType("Billing");

        when(addressRepository.findByPhoneNumberAndAddressTypeAndDeletedFalse(
                addressDto.getPhoneNumber(),
                addressDto.getAddressType()
        )).thenReturn(existingAddress);

        AddressDto result = addressService.update(addressDto);

        assertTrue(result.isSuccess());
        assertEquals("Address updated successfully", result.getMessage());

        verify(modelMapper).map(addressDto, existingAddress);
        verify(addressRepository).save(existingAddress);
    }

    @Test
    void testUpdate_AddressNotFound_SaveNewAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNumber(9876543210L);
        addressDto.setAddressType("Shipping");

        Address address = new Address();
        address.setPhoneNumber(9876543210L);
        address.setAddressType("Shipping");

        when(addressRepository.findByPhoneNumberAndAddressTypeAndDeletedFalse(
                addressDto.getPhoneNumber(),
                addressDto.getAddressType()
        )).thenReturn(null);

        when(modelMapper.map(addressDto, Address.class))
                .thenReturn(address);

        AddressDto result = addressService.update(addressDto);

        assertTrue(result.isSuccess());
        assertEquals("Address saved successfully", result.getMessage());

        verify(addressRepository).save(address);
    }

    @Test
    void testFindAll_Success() {
        Address address = new Address();
        address.setPhoneNumber(9876543210L);
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNumber(9876543210L);

        when(addressRepository.findByDeletedFalse()).thenReturn(List.of(address));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(addressDto));
        List<AddressDto> result = addressService.findAll();
        assertEquals(1, result.size());
        assertEquals(9876543210L, result.get(0).getPhoneNumber());
    }

    @Test
    void testDeleteByPhoneNumber_Success() {
        Long phoneNumber = 9876543210L;

        Address address1 = new Address();
        address1.setPhoneNumber(phoneNumber);
        address1.setDeleted(false);

        Address address2 = new Address();
        address2.setPhoneNumber(phoneNumber);
        address2.setDeleted(false);
        List<Address> addressList = List.of(address1, address2);
        when(addressRepository.findByPhoneNumberAndDeletedFalse(phoneNumber)).thenReturn(addressList);
        addressService.deleteByPhoneNumber(phoneNumber);
        assertTrue(address1.getDeleted());
        assertTrue(address2.getDeleted());
        verify(addressRepository).findByPhoneNumberAndDeletedFalse(phoneNumber);
        verify(addressRepository).saveAll(addressList);
    }

}
