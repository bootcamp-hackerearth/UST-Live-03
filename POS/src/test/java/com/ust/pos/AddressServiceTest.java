package com.ust.pos;

import com.ust.pos.adress.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import java.util.ArrayList;
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
        addressDto.setIdentifier("ADR-001");

        address = new Address();
        address.setIdentifier("ADR-001");
        address.setStatus(true);
        address.setDeleted(false);
    }

    @Test
    @DisplayName("Save Address - Success")
    void save_Success() {
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(null).thenReturn(address);
        when(modelMapper.map(addressDto, Address.class)).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("added Successfully"));
        verify(addressRepository).save(address);
        verify(modelMapper).map(address, addressDto);
    }

    @Test
    @DisplayName("Save Address - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        address.setDeleted(false);
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("Save Address - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        address.setDeleted(true);
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(address);

        AddressDto result = addressService.save(addressDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("Update Address - Success")
    void update_Success() {
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(address).thenReturn(address);

        AddressDto result = addressService.update(addressDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Updated"));
        verify(addressRepository).save(address);
        verify(modelMapper).map(addressDto, address);
        verify(modelMapper).map(address, addressDto);
    }

    @Test
    @DisplayName("Update Address - Failure: Not Found")
    void update_Failure_NotFound() {
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(null);

        AddressDto result = addressService.update(addressDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    @DisplayName("Delete Address - Success")
    void delete_Success() {
        List<Address> addresses = new ArrayList<>(List.of(address));
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(addresses);

        boolean result = addressService.delete("1234567890");

        Assertions.assertTrue(result);
        verify(addressRepository).saveAll(addresses);
    }

    @Test
    @DisplayName("Delete Address - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(null);

        boolean result = addressService.delete("1234567890");

        Assertions.assertFalse(result);
        verify(addressRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Find All Addresses - Success")
    void findAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> addressPage = new PageImpl<>(List.of(address));

        when(addressRepository.findAll(pageable)).thenReturn(addressPage);
        when(modelMapper.map(eq(addressPage.getContent()), any(Type.class))).thenReturn(List.of(addressDto));

        List<AddressDto> result = addressService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(address);
        when(modelMapper.map(address, AddressDto.class)).thenReturn(addressDto);

        AddressDto result = addressService.findByIdentifier("ADR-001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(addressRepository.findByIdentifier("ADR-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> addressService.findByIdentifier("ADR-001"));
    }

    @Test
    @DisplayName("Find All By Phone Number - Success")
    void findAllByPhoneNumber_Success() {
        List<Address> addresses = List.of(address);
        when(addressRepository.findAllByPhoneNoAndDeletedFalse("1234567890")).thenReturn(addresses);
        when(modelMapper.map(eq(addresses), any(Type.class))).thenReturn(List.of(addressDto));

        List<AddressDto> result = addressService.findAllByPhoneNumber("1234567890");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }
}