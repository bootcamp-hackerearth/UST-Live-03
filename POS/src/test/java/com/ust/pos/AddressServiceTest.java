package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("billing");
        Address address = new Address();
        Mockito.when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        Mockito.when(addressRepository.save(address)).thenReturn(address);
        AddressDto response = addressService.save(addressDto);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Successfully added the address", response.getMessage());
        Mockito.verify(addressRepository).save(address);
    }

    @Test
    void updateTest() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("billing");
        addressDto.setCity("Chennai");
        Address existingAddress = new Address();
        Mockito.when(addressRepository.findByPhoneNoAndAddressType("1234567890", "billing")
        ).thenReturn(existingAddress);
        Mockito.doNothing().when(modelMapper).map(addressDto, existingAddress);
        Mockito.when(addressRepository.save(existingAddress)).thenReturn(existingAddress);
        addressService.update(addressDto);
        Mockito.verify(addressRepository).findByPhoneNoAndAddressType("1234567890", "billing");
        Mockito.verify(addressRepository).save(existingAddress);
    }

    @Test
    void findByPhoneNoAndAddressTypeTest() {
        Address address = new Address();
        address.setPhoneNo("1234567890");
        address.setAddressType("billing");
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("billing");
        Mockito.when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse
                ("1234567890", "billing")).thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);
        AddressDto response = addressService.findByPhoneNoAndAddressType("1234567890", "billing");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("1234567890", response.getPhoneNo());
    }

    @Test
    void findByPhoneAndAddressTypeNotFoundTest() {
        Mockito.when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse
                ("1234567890", "billing")).thenReturn(null);
        AddressDto response = addressService.findByPhoneNoAndAddressType
                ("1234567890", "billing");
        Assertions.assertNull(response);
    }

    @Test
    void deleteTest() {
        Address billing = new Address();
        billing.setPhoneNo("1234567890");
        billing.setAddressType("billing");
        Address shipping = new Address();
        shipping.setPhoneNo("1234567890");
        shipping.setAddressType("shipping");
        Mockito.when(addressRepository.findByPhoneNoAndAddressType
                ("1234567890", "billing")).thenReturn(billing);
        Mockito.when(addressRepository.findByPhoneNoAndAddressType
                ("1234567890", "shipping")).thenReturn(shipping);
        Mockito.when(addressRepository.save(billing)).thenReturn(billing);
        Mockito.when(addressRepository.save(shipping)).thenReturn(shipping);
        addressService.delete("1234567890");
        Assertions.assertTrue(billing.isDeleted());
        Assertions.assertTrue(shipping.isDeleted());
        Mockito.verify(addressRepository).save(billing);
        Mockito.verify(addressRepository).save(shipping);
    }

    @Test
    void deleteOnlyBillingExistsTest() {
        Address billing = new Address();
        billing.setPhoneNo("1234567890");
        billing.setAddressType("billing");
        Mockito.when(addressRepository.findByPhoneNoAndAddressType
                ("1234567890", "billing")).thenReturn(billing);
        Mockito.when(addressRepository.findByPhoneNoAndAddressType
                ("1234567890", "shipping")).thenReturn(null);
        Mockito.when(addressRepository.save(billing)).thenReturn(billing);
        addressService.delete("1234567890");
        Assertions.assertTrue(billing.isDeleted());
        Mockito.verify(addressRepository).save(billing);
        Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any(Address.class));
    }

    @Test
    void deleteNoAddressFoundTest() {
        Mockito.when(addressRepository.findByPhoneNoAndAddressType
                ("1234567890", "billing")).thenReturn(null);
        Mockito.when(addressRepository.findByPhoneNoAndAddressType
                ("1234567890", "shipping")).thenReturn(null);
        addressService.delete("1234567890");
        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any(Address.class));
    }
}