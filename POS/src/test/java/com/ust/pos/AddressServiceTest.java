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
import java.util.ArrayList;
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
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(999999L);
        dto.setAddressType("billingAddress");
        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressTypeAndDeletedFalse(999999L, "billingAddress"))
                .thenReturn(null);
        Address address = new Address();
        Mockito.when(modelMapper.map(dto, Address.class))
                .thenReturn(address);
        Mockito.when(addressRepository.save(address))
                .thenReturn(address);
        AddressDto response = addressService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(999999L);
        dto.setAddressType("billingAddress");
        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressTypeAndDeletedFalse(999999L, "billingAddress"))
                .thenReturn(new Address());
        AddressDto response = addressService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(999999L);
        dto.setAddressType("billingAddress");
        Address existing = new Address();
        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressTypeAndDeletedFalse(999999L, "billingAddress"))
                .thenReturn(existing);
        Mockito.doNothing().when(modelMapper)
                .map(dto, existing);
        Mockito.when(addressRepository.save(existing))
                .thenReturn(existing);
        AddressDto response = addressService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo(999999L);
        dto.setAddressType("billingAddress");
        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressTypeAndDeletedFalse(999999L, "billingAddress"))
                .thenReturn(null);
        AddressDto response = addressService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByPhoneNoAndAddressTypeTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressTypeAndDeletedFalse(999999L, "billingAddress"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);
        AddressDto response =
                addressService.findByPhoneNoAndAddressType(999999L, "billingAddress");
        Assertions.assertNotNull(response);
    }

    @Test
    void findByPhoneNoAndAddressTypeNullTest() {
        Mockito.when(addressRepository
                        .findByPhoneNoAndAddressTypeAndDeletedFalse(999999L, "billingAddress"))
                .thenReturn(null);
        AddressDto response =
                addressService.findByPhoneNoAndAddressType(999999L, "billingAddress");
        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();
        List<Address> list = List.of(address);
        List<AddressDto> dtoList = List.of(dto);
        Mockito.when(addressRepository.findByDeletedFalse())
                .thenReturn(list);
        Mockito.when(modelMapper.map(
                        Mockito.eq(list),
                        Mockito.any(Type.class)))
                .thenReturn(dtoList);
        List<AddressDto> response = addressService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteByPhoneTest() {
        Address address1 = new Address();
        Address address2 = new Address();
        List<Address> addresses = new ArrayList<>();
        addresses.add(address1);
        addresses.add(address2);
        Mockito.when(addressRepository.findByPhoneNoAndDeletedFalse(999999L))
                .thenReturn(addresses);
        Mockito.when(addressRepository.saveAll(addresses))
                .thenReturn(addresses);
        addressService.deleteByPhone(999999L);
        Mockito.verify(addressRepository)
                .findByPhoneNoAndDeletedFalse(999999L);
        Mockito.verify(addressRepository)
                .saveAll(addresses);
    }

    @Test
    void deleteByPhoneEmptyListTest() {
        List<Address> addresses = new ArrayList<>();
        Mockito.when(addressRepository.findByPhoneNoAndDeletedFalse(999999L))
                .thenReturn(addresses);
        Mockito.when(addressRepository.saveAll(addresses))
                .thenReturn(addresses);
        addressService.deleteByPhone(999999L);
        Mockito.verify(addressRepository)
                .saveAll(addresses);
    }
}