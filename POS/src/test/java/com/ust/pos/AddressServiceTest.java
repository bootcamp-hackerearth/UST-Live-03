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
        addressDto.setAddressType("HOME");

        Address address = new Address();

        Mockito.when(modelMapper.map(addressDto, Address.class))
                .thenReturn(address);
        Mockito.when(addressRepository.save(address))
                .thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("1234567890", response.getPhoneNo());
    }


    @Test
    void updateTest_WhenAddressExists() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("HOME");

        Address existingAddress = new Address();

        Mockito.when(
                addressRepository.findByPhoneNoAndAddressType("1234567890", "HOME")
        ).thenReturn(existingAddress);

        Mockito.doNothing()
                .when(modelMapper)
                .map(addressDto, existingAddress);

        Mockito.when(addressRepository.save(existingAddress))
                .thenReturn(existingAddress);

        addressService.update(addressDto);

        Mockito.verify(addressRepository)
                .findByPhoneNoAndAddressType("1234567890", "HOME");
        Mockito.verify(modelMapper)
                .map(addressDto, existingAddress);
        Mockito.verify(addressRepository)
                .save(existingAddress);
    }


    @Test
    void updateTest_WhenAddressNotFound_CreateNew() {
        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("HOME");

        Address newAddress = new Address();

        Mockito.when(
                addressRepository.findByPhoneNoAndAddressType("1234567890", "HOME")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(addressDto, Address.class)
        ).thenReturn(newAddress);

        Mockito.when(addressRepository.save(newAddress))
                .thenReturn(newAddress);

        addressService.update(addressDto);

        Mockito.verify(modelMapper)
                .map(addressDto, Address.class);
        Mockito.verify(addressRepository)
                .save(newAddress);

        Mockito.verify(modelMapper, Mockito.never())
                .map(Mockito.eq(addressDto), Mockito.any(Address.class));
    }


    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(addressRepository)
                .deleteByPhoneNo("1234567890");

        addressService.delete("1234567890");

        Mockito.verify(addressRepository)
                .deleteByPhoneNo("1234567890");
    }


    @Test
    void findByPhoneAndAddressTypeTest() {
        Address address = new Address();
        address.setPhoneNo("1234567890");
        address.setAddressType("HOME");

        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo("1234567890");
        addressDto.setAddressType("HOME");

        Mockito.when(
                addressRepository.findByPhoneNoAndAddressType("1234567890", "HOME")
        ).thenReturn(address);

        Mockito.when(
                modelMapper.map(address, AddressDto.class)
        ).thenReturn(addressDto);

        AddressDto response =
                addressService.findByPhoneAndAddressType("1234567890", "HOME");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("1234567890", response.getPhoneNo());
        Assertions.assertEquals("HOME", response.getAddressType());
    }

    @Test
    void findByPhoneAndAddressTypeTest_NotFound() {
        Mockito.when(
                addressRepository.findByPhoneNoAndAddressType("1234567890", "HOME")
        ).thenReturn(null);

        AddressDto response =
                addressService.findByPhoneAndAddressType("1234567890", "HOME");

        Assertions.assertNull(response);
    }
}