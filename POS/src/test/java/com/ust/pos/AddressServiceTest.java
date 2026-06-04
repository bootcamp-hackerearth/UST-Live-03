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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void findByPhoneSuccess() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "billing"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);
        AddressDto response = addressService.findByPhoneNoAndAddressType(123L, "billing");
        Assertions.assertNotNull(response);
    }

    @Test
    void saveTestSuccess() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("billing");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "billing"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Address.class))
                .thenReturn(new Address());
        AddressDto response = addressService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void saveTestDuplicate() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("billing");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "billing"))
                .thenReturn(new Address());

        AddressDto response = addressService.save(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTestSuccess() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("billing");
        Address existing = new Address();

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "billing"))
                .thenReturn(existing);

        AddressDto response = addressService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        verify(addressRepository).save(existing);
    }

    @Test
    void updateTestFailure() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("billing");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(123L, "billing"))
                .thenReturn(null);

        AddressDto response = addressService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findAllTest() {

        List<Address> list = List.of(new Address());
        List<AddressDto> dtoList = List.of(new AddressDto());

        Mockito.when(addressRepository.findAll())
                .thenReturn(list);
        Mockito.when(modelMapper.map(eq(list), any(Type.class)))
                .thenReturn(dtoList);

        List<AddressDto> response = addressService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {

        addressService.deleteByPhoneNo(123L);
        verify(addressRepository).deleteByPhoneNo(123L);
    }
}