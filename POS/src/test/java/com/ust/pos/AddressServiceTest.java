package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void saveSuccess() {
        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR1");
        Mockito.when(addressRepository.findByIdentifier("ADDR1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Address.class))
                .thenReturn(new Address());
        AddressDto response = addressService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailureAlreadyExists() {
        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1"))
                .thenReturn(new Address());

        AddressDto response = addressService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void findByIdSuccess() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findById(1L))
                .thenReturn(Optional.of(address));
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response = addressService.findById(1L);

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdFailure_shouldThrowException() {

        Mockito.when(addressRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> addressService.findById(99L)
        );

        Assertions.assertTrue(exception.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void updateSuccess() {
        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR1");
        dto.setAddressType("Billing");

        Address existingAddress = new Address();

        Mockito.when(addressRepository.findByIdentifierAndAddressType("ADDR1", "Billing"))
                .thenReturn(existingAddress);

        AddressDto response = addressService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateFailureAddressNotFound() {
        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR1");
        dto.setAddressType("Billing");

        Mockito.when(addressRepository.findByIdentifierAndAddressType("ADDR1", "Billing"))
                .thenReturn(null);

        AddressDto response = addressService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void findByIdentifierAndAddressTypeSuccess() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findByIdentifierAndAddressType("ADDR1", "Billing"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response =
                addressService.findByIdentifierAndAddressType("ADDR1", "Billing");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierAndAddressTypeNotFound() {
        Mockito.when(addressRepository.findByIdentifierAndAddressType("ADDR1", "Billing"))
                .thenReturn(null);

        AddressDto response =
                addressService.findByIdentifierAndAddressType("ADDR1", "Billing");

        Assertions.assertNull(response);
    }

    @Test
    void findByIdentifier() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findByIdentifier("ADDR1"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response = addressService.findByIdentifier("ADDR1");

        Assertions.assertNotNull(response);
    }
}