package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("1234567890");
        Address address = new Address();

        Mockito.when(modelMapper.map(dto, Address.class)).thenReturn(address);
        Mockito.when(addressRepository.save(address)).thenReturn(address);

        AddressDto response = addressService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("1234567890", response.getPhoneNo());
        Mockito.verify(addressRepository).save(address);
    }

    @Test
    void updateTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("1234567890");
        dto.setAddressType("billing");

        Address existing = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType("1234567890", "billing"))
                .thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(addressRepository.save(existing)).thenReturn(existing);
        addressService.update(dto);
        Mockito.verify(addressRepository).findByPhoneNoAndAddressType("1234567890", "billing");
        Mockito.verify(addressRepository).save(existing);
    }

    @Test
    void updateTest_whenAddressNotFound() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("123");
        dto.setAddressType("billing");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType("123", "billing"))
                .thenReturn(null);
        Assertions.assertDoesNotThrow(() -> addressService.update(dto));
    }

    @Test
    void deleteTest() {
        Address billing = new Address();
        billing.setDeleted(false);

        Address shipping = new Address();
        shipping.setDeleted(false);

        Mockito.when(addressRepository.findByPhoneNoAndAddressType("1234567890", "billing"))
                .thenReturn(billing);
        Mockito.when(addressRepository.findByPhoneNoAndAddressType("1234567890", "shipping"))
                .thenReturn(shipping);
        Mockito.when(addressRepository.save(Mockito.any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        addressService.delete("1234567890");

        Mockito.verify(addressRepository).findByPhoneNoAndAddressType("1234567890", "billing");
        Mockito.verify(addressRepository).findByPhoneNoAndAddressType("1234567890", "shipping");
        Mockito.verify(addressRepository, Mockito.times(2)).save(Mockito.any(Address.class));
        Assertions.assertTrue(billing.isDeleted());
        Assertions.assertTrue(shipping.isDeleted());
    }

    @Test
    void deleteTest_partial() {
        Address billing = new Address();
        Mockito.when(addressRepository.findByPhoneNoAndAddressType("123", "billing"))
                .thenReturn(billing);
        Mockito.when(addressRepository.findByPhoneNoAndAddressType("123", "shipping"))
                .thenReturn(null);
        addressService.delete("123");
        Mockito.verify(addressRepository).save(billing);
        Mockito.verify(addressRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void findByPhoneAndAddressTypeTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse("1234567890", "billing"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);
        AddressDto response = addressService.findByPhoneAndAddressType("1234567890", "billing");
        Assertions.assertNotNull(response);
    }

    @Test
    void findByPhoneAndAddressTypeNotFoundTest() {
        Mockito.when(addressRepository.findByPhoneNoAndAddressTypeAndDeletedFalse("1234567890", "billing")).thenReturn(null);
        AddressDto response = addressService.findByPhoneAndAddressType("1234567890", "billing");
        Assertions.assertNull(response);
    }
}