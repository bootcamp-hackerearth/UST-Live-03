package com.ust.pos;

import com.ust.pos.customer.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByPhoneNoAndAddressType_Found() {

        Address address = new Address();
        address.setPhoneNo(9876543210L);
        address.setAddressType("billingAddress");

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(address);

        when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto result =
                addressService.findByPhoneNoAndAddressType(
                        9876543210L,
                        "billingAddress");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "billingAddress",
                result.getAddressType());
    }

    @Test
    void findByPhoneNoAndAddressType_NotFound() {

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(null);

        AddressDto result =
                addressService.findByPhoneNoAndAddressType(
                        9876543210L,
                        "billingAddress");

        Assertions.assertNotNull(result);
    }

    @Test
    void save_NewAddress() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        Address address = new Address();

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(null);

        when(modelMapper.map(dto, Address.class))
                .thenReturn(address);

        when(addressRepository.save(address))
                .thenReturn(address);

        AddressDto result = addressService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "billingAddress",
                result.getAddressType());

        verify(addressRepository).save(address);
    }

    @Test
    void save_AddressExists() {

        Address existing = new Address();

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(existing);

        AddressDto result = addressService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(addressRepository, never()).save(any());
    }

    @Test
    void update_AddressExists() {

        Address existing = new Address();

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(existing);

        when(addressRepository.save(existing))
                .thenReturn(existing);

        AddressDto result = addressService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                "billingAddress",
                result.getAddressType());

        verify(modelMapper).map(dto, existing);
        verify(addressRepository).save(existing);
    }

    @Test
    void update_AddressNotFound() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9876543210L);
        dto.setAddressType("billingAddress");

        when(addressRepository.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(null);

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(addressRepository, never()).save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Address> addresses =
                List.of(new Address(), new Address());

        Page<Address> page =
                new PageImpl<>(addresses);

        List<AddressDto> dtoList =
                List.of(new AddressDto(),
                        new AddressDto());

        when(addressRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(addresses), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<AddressDto> result =
                addressService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(
                2,
                result.getContent().size());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Address> emptyList = List.of();

        Page<Address> page =
                new PageImpl<>(emptyList);

        when(addressRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(emptyList), any(Type.class)))
                .thenReturn(List.of());

        WsDto<AddressDto> result =
                addressService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());

        verify(addressRepository).findAll(pageable);
    }

    @Test
    void deleteByPhoneNoTest() {

        doNothing().when(addressRepository)
                .deleteByPhoneNo(9876543210L);

        addressService.deleteByPhoneNo(9876543210L);

        verify(addressRepository)
                .deleteByPhoneNo(9876543210L);
    }
}