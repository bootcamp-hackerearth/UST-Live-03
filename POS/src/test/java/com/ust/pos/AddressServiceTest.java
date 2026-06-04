package com.ust.pos;

import com.ust.pos.adress.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    @DisplayName("Save - Success Case")
    void saveTestSuccess() {
        AddressDto inputDto = new AddressDto();
        inputDto.setIdentifier("ADDR001");

        Address addressEntity = new Address();
        addressEntity.setIdentifier("ADDR001");

        Mockito.when(addressRepository.findByIdentifier("ADDR001"))
                .thenReturn(null)
                .thenReturn(addressEntity);

        Mockito.when(modelMapper.map(inputDto, Address.class)).thenReturn(addressEntity);

        AddressDto response = addressService.save(inputDto);

        Assertions.assertNotNull(response);
        Mockito.verify(addressRepository).save(addressEntity);
        Mockito.verify(modelMapper).map(addressEntity, inputDto);
    }

    @Test
    @DisplayName("Save - Already Exists Case")
    void saveTestAlreadyExists() {
        AddressDto inputDto = new AddressDto();
        inputDto.setIdentifier("ADDR001");

        Mockito.when(addressRepository.findByIdentifier("ADDR001")).thenReturn(new Address());

        AddressDto response = addressService.save(inputDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Address with identifier - ADDR001 already exists", response.getMessage());
        Mockito.verify(addressRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update - Success Case")
    void updateTestSuccess() {
        AddressDto inputDto = new AddressDto();
        inputDto.setIdentifier("ADDR001");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("ADDR001");

        Mockito.when(addressRepository.findByIdentifier("ADDR001")).thenReturn(existingAddress);

        AddressDto response = addressService.update(inputDto);

        Assertions.assertNotNull(response);
        Mockito.verify(modelMapper).map(inputDto, existingAddress);
        Mockito.verify(addressRepository).save(existingAddress);
        Mockito.verify(modelMapper).map(existingAddress, inputDto);
    }

    @Test
    @DisplayName("Update - Not Found Case")
    void updateTestNotFound() {
        AddressDto inputDto = new AddressDto();
        inputDto.setIdentifier("ADDR001");

        Mockito.when(addressRepository.findByIdentifier("ADDR001")).thenReturn(null);

        AddressDto response = addressService.update(inputDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
        Mockito.verify(addressRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Delete - Success Case")
    void deleteTest() {
        String phoneNo = "1234567890";
        List<Address> addresses = List.of(new Address());

        Mockito.when(addressRepository.findAllByPhoneNo(phoneNo)).thenReturn(addresses);

        boolean result = addressService.delete(phoneNo);

        Assertions.assertTrue(result);
        Mockito.verify(addressRepository).deleteAll(addresses);
    }

    @Test
    @DisplayName("Find All - Pagination Case")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Address> addressList = List.of(new Address());
        Page<Address> addressPage = new PageImpl<>(addressList);
        List<AddressDto> expectedDtos = List.of(new AddressDto());

        Mockito.when(addressRepository.findAll(pageable)).thenReturn(addressPage);
        Mockito.when(modelMapper.map(eq(addressList), any(Type.class))).thenReturn(expectedDtos);

        List<AddressDto> response = addressService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Mockito.verify(addressRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        Address address = new Address();
        AddressDto expectedDto = new AddressDto();
        expectedDto.setIdentifier("ADDR001");

        Mockito.when(addressRepository.findByIdentifier("ADDR001")).thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(expectedDto);

        AddressDto response = addressService.findByIdentifier("ADDR001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADDR001", response.getIdentifier());
    }

    @Test
    @DisplayName("Find All By Phone Number - Success")
    void findAllByPhoneNumberTest() {
        String phoneNo = "9876543210";
        List<Address> addresses = List.of(new Address());
        List<AddressDto> expectedDtos = List.of(new AddressDto());

        Mockito.when(addressRepository.findAllByPhoneNo(phoneNo)).thenReturn(addresses);
        Mockito.when(modelMapper.map(eq(addresses), any(Type.class))).thenReturn(expectedDtos);

        List<AddressDto> response = addressService.findAllByPhoneNumber(phoneNo);

        Assertions.assertEquals(1, response.size());
        Mockito.verify(addressRepository).findAllByPhoneNo(phoneNo);
    }
}