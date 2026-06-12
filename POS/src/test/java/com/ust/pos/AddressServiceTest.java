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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByPhoneNoAndAddressTypeSuccessTest() {

        Address address = new Address();
        address.setAddressType("HOME");

        AddressDto dto = new AddressDto();
        dto.setAddressType("HOME");
        dto.setSuccess(true);

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "HOME"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto result =
                addressService.findByPhoneNoAndAddressType(123L, "HOME");

        Assertions.assertNotNull(result);

        Assertions.assertEquals("HOME", result.getAddressType());

        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void findByPhoneNoAndAddressTypeFailureTest() {

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "HOME"))
                .thenReturn(null);

        AddressDto result =
                addressService.findByPhoneNoAndAddressType(123L, "HOME");

        Assertions.assertNull(result);
    }

    @Test
    void saveTestSuccess() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("HOME");

        Address address = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "HOME"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Address.class))
                .thenReturn(address);

        AddressDto result = addressService.save(dto);

        Assertions.assertEquals("HOME", result.getAddressType());

        verify(addressRepository).save(address);
    }

    @Test
    void saveTestFailure() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("HOME");

        Address address = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "HOME"))
                .thenReturn(address);

        AddressDto result = addressService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(addressRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("HOME");

        Address address = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "HOME"))
                .thenReturn(address);

        AddressDto result = addressService.update(dto);

        Assertions.assertEquals("HOME", result.getAddressType());

        verify(modelMapper).map(dto, address);

        verify(addressRepository).save(address);
    }

    @Test
    void updateTestFailure() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("HOME");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "HOME"))
                .thenReturn(null);

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(addressRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {

        List<Address> addresses =
                List.of(new Address(), new Address());

        List<AddressDto> dtoList =
                List.of(new AddressDto(), new AddressDto());

        Mockito.when(addressRepository.findAll())
                .thenReturn(addresses);

        Mockito.when(modelMapper.map(
                Mockito.eq(addresses),
                Mockito.any(Type.class)
        )).thenReturn(dtoList);

        List<AddressDto> result = addressService.findAll();

        Assertions.assertEquals(2, result.size());

        verify(addressRepository).findAll();
    }
}