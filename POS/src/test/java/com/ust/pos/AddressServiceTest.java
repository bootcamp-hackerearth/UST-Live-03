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
    void findByPhoneNoAndAddressTypeTest() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response = addressService
                .findByPhoneNoAndAddressType(123L, "BILLING");

        Assertions.assertNotNull(response);
    }

    @Test
    void saveTest_Success() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("BILLING");

        Address address = new Address();

        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Address.class))
                .thenReturn(address);

        AddressDto response = addressService.save(dto);

        Assertions.assertEquals("BILLING", response.getAddressType());
        Mockito.verify(addressRepository).save(address);
    }

    @Test
    void saveTest_Duplicate() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("BILLING");

        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(new Address());

        AddressDto response = addressService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));

        Mockito.verify(addressRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTest_Success() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("BILLING");

        Address existing = new Address();

        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(existing);

        AddressDto response = addressService.update(dto);

        Assertions.assertNotNull(response);
        Mockito.verify(addressRepository).save(existing);
    }

    @Test
    void updateTest_NotFound() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(123L);
        dto.setAddressType("BILLING");

        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressType(123L, "BILLING"))
                .thenReturn(null);

        AddressDto response = addressService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void findAllTest() {

        List<Address> list = List.of(new Address());
        List<AddressDto> dtoList = List.of(new AddressDto());

        Mockito.when(addressRepository.findAll())
                .thenReturn(list);

        Mockito.when(modelMapper.map(
                Mockito.eq(list),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<AddressDto> response = addressService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {

        addressService.deleteByPhone(123L);

        Mockito.verify(addressRepository)
                .deleteByPhoneNo(123L);
    }
}
