package com.ust.pos;

import com.ust.pos.adress.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressDto addressDto;
    private Address address;

    @BeforeEach
    void setUp() {
        addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR-01");
        addressDto.setPhoneNo("1234567890");

        address = new Address();
        address.setIdentifier("ADDR-01");
        address.setPhoneNo("1234567890");
        address.setDeleted(false);
    }

    @Test
    @DisplayName("Save Address - Success")
    void save_Success() {
        when(addressRepository.findByIdentifier("ADDR-01")).thenReturn(null);
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("added Successfully"));
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    @DisplayName("Save Address - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        address.setDeleted(false);
        when(addressRepository.findByIdentifier("ADDR-01")).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Save Address - Failure: Previously Deleted")
    void save_Failure_PreviouslyDeleted() {
        address.setDeleted(true);
        when(addressRepository.findByIdentifier("ADDR-01")).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
    }

    @Test
    @DisplayName("Update Address - Success")
    void update_Success() {
        when(addressRepository.findByIdentifier("ADDR-01")).thenReturn(address).thenReturn(address);

        AddressDto result = addressService.update(addressDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Updated"));
        verify(addressRepository).save(address);
    }

    @Test
    @DisplayName("Update Address - Failure: Not Found")
    void update_Failure_NotFound() {
        when(addressRepository.findByIdentifier("ADDR-01")).thenReturn(null);

        AddressDto result = addressService.update(addressDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    @DisplayName("Delete Addresses By Phone - Success")
    void delete_Success() {
        List<Address> addresses = List.of(address);
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(addresses);

        boolean result = addressService.delete("1234567890");

        Assertions.assertTrue(result);
        verify(addressRepository).saveAll(addresses);
    }

    @Test
    @DisplayName("Delete Addresses By Phone - Failure: Null Return")
    void delete_Failure_Null() {
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(null);

        boolean result = addressService.delete("1234567890");

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Find All Address - Success")
    void findAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> page = new PageImpl<>(List.of(address));
        when(addressRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(addressDto));

        List<AddressDto> result = addressService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(addressRepository.findByIdentifier("ADDR-01")).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto result = addressService.findByIdentifier("ADDR-01");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find All By Phone Number - Success")
    void findAllByPhoneNumber_Success() {
        List<Address> addresses = List.of(address);
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(addresses);
        when(modelMapper.map(eq(addresses), any(Type.class))).thenReturn(List.of(addressDto));

        List<AddressDto> result = addressService.findAllByPhoneNumber("1234567890");

        Assertions.assertEquals(1, result.size());
    }
}