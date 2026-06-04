package com.ust.pos;

import com.ust.pos.customer.service.impl.AddressServiceImpl;
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

import java.util.List;

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
        addressDto.setPhoneNo(1234567890L);
        addressDto.setAddressType("billingAddress");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(1234567890L, "billingAddress"))
                .thenReturn(null);

        Address address = new Address();
        Mockito.when(modelMapper.map(addressDto, Address.class)).thenReturn(address);
        Mockito.when(addressRepository.save(address)).thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {

        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo(1234567890L);
        addressDto.setAddressType("billingAddress");

        Address address = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(1234567890L, "billingAddress"))
                .thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo(1234567890L);
        addressDto.setAddressType("billingAddress");

        Address existingAddress = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(1234567890L, "billingAddress"))
                .thenReturn(existingAddress);

        Mockito.when(addressRepository.save(existingAddress)).thenReturn(existingAddress);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertNull(response.getMessage());
    }

    @Test
    void updateTestFailure() {

        AddressDto addressDto = new AddressDto();
        addressDto.setPhoneNo(1234567890L);
        addressDto.setAddressType("billingAddress");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(1234567890L, "billingAddress"))
                .thenReturn(null);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByPhoneNoAndAddressTypeTest() {

        Address address = new Address();
        AddressDto addressDto = new AddressDto();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(1234567890L, "billingAddress"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(addressDto);

        AddressDto response = addressService.findByPhoneNoAndAddressType(1234567890L, "billingAddress");

        Assertions.assertNotNull(response);
    }

    @Test
    void findAllTest() {

        Address address = new Address();
        AddressDto addressDto = new AddressDto();

        List<Address> addresses = List.of(address);
        List<AddressDto> addressDtos = List.of(addressDto);

        Mockito.when(addressRepository.findAll()).thenReturn(addresses);
        Mockito.when(modelMapper.map(
                Mockito.eq(addresses),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(addressDtos);

        List<AddressDto> response = addressService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteByPhoneTest() {

        Mockito.doNothing().when(addressRepository).deleteByPhoneNo(1234567890L);

        addressService.deleteByPhone(1234567890L);

        Mockito.verify(addressRepository).deleteByPhoneNo(1234567890L);
    }
}