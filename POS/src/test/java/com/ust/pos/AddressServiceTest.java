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
        address.setPhoneNo(9999999999L);

        AddressDto dto = new AddressDto();

        when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "HOME"))
                .thenReturn(address);

        when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto result =
                addressService.findByPhoneNoAndAddressType(9999999999L, "HOME");

        Assertions.assertNotNull(result);
    }

    @Test
    void findByPhoneNoAndAddressType_NotFound() {

        when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "HOME"))
                .thenReturn(null);

        AddressDto result =
                addressService.findByPhoneNoAndAddressType(9999999999L, "HOME");

        Assertions.assertNotNull(result);
    }

    @Test
    void save_NewAddress() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9999999999L);
        dto.setAddressType("HOME");

        Address address = new Address();

        when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "HOME"))
                .thenReturn(null);

        when(modelMapper.map(dto, Address.class))
                .thenReturn(address);

        when(addressRepository.save(address))
                .thenReturn(address);

        AddressDto result = addressService.save(dto);

        Assertions.assertNotNull(result);
        verify(addressRepository).save(address);
    }

    @Test
    void save_AddressExists() {

        Address existing = new Address();

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9999999999L);
        dto.setAddressType("HOME");

        when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "HOME"))
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
        dto.setPhoneNo(9999999999L);
        dto.setAddressType("HOME");

        when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "HOME"))
                .thenReturn(existing);

        when(addressRepository.save(existing))
                .thenReturn(existing);

        AddressDto result = addressService.update(dto);

        Assertions.assertNotNull(result);
        verify(modelMapper).map(dto, existing);
        verify(addressRepository).save(existing);
    }

    @Test
    void update_AddressNotFound() {

        AddressDto dto = new AddressDto();
        dto.setPhoneNo(9999999999L);
        dto.setAddressType("HOME");

        when(addressRepository.findByPhoneNoAndAddressType(9999999999L, "HOME"))
                .thenReturn(null);

        AddressDto result = addressService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(addressRepository, never()).save(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Address address1 = new Address();
        Address address2 = new Address();

        Page<Address> page = new PageImpl<>(
                List.of(address1, address2),
                pageable,
                2
        );

        List<AddressDto> dtoList = List.of(new AddressDto(), new AddressDto());

        when(addressRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<AddressDto> result = addressService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(addressRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> page = new PageImpl<>(List.of(), pageable, 0);

        when(addressRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<AddressDto> result = addressService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(addressRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void deleteByPhoneNo_Test() {

        Address address = new Address();

        when(addressRepository.findByPhoneNo(9999999999L))
                .thenReturn((List<Address>) address);

        addressService.deleteByPhoneNo(9999999999L);

        verify(addressRepository).findByPhoneNo(9999999999L);
    }
}
