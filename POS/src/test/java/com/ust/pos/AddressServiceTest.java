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
    void saveSuccessTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address address = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(addressDto, Address.class))
                .thenReturn(address);

        AddressDto response = addressService.save(addressDto);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(addressRepository).save(address);
    }

    @Test
    void saveFailureTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(new Address());

        AddressDto response = addressService.save(addressDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Address existingAddress = new Address();
        existingAddress.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(existingAddress);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertEquals("ADDR1", response.getIdentifier());
        verify(addressRepository).save(existingAddress);
    }

    @Test
    void updateFailureTest() {

        AddressDto addressDto = new AddressDto();
        addressDto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        AddressDto response = addressService.update(addressDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteByPhoneNoSuccessTest() {

        Address addr1 = new Address();
        Address addr2 = new Address();

        List<Address> addresses = List.of(addr1, addr2);

        Mockito.when(addressRepository.findByPhoneNo("9999999999")).thenReturn(addresses);

        addressService.delete("9999999999");

        verify(addressRepository).deleteAll(addresses);
    }

    @Test
    void deleteByPhoneNoEmptyListTest() {

        Mockito.when(addressRepository.findByPhoneNo("9999999999")).thenReturn(List.of());

        addressService.delete("9999999999");

        verify(addressRepository).deleteAll(List.of());
    }

    @Test
    void findByIdentifierSuccessTest() {

        Address address = new Address();
        address.setIdentifier("ADDR1");

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR1");

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class)).thenReturn(dto);

        AddressDto response = addressService.findByIdentifier("ADDR1");

        Assertions.assertEquals("ADDR1", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(addressRepository.findByIdentifier("ADDR1")).thenReturn(null);

        AddressDto response = addressService.findByIdentifier("ADDR1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Address a1 = new Address();
        Address a2 = new Address();

        List<Address> addresses = List.of(a1, a2);

        AddressDto d1 = new AddressDto();
        AddressDto d2 = new AddressDto();

        List<AddressDto> addressDtos = List.of(d1, d2);

        Mockito.when(addressRepository.findAll()).thenReturn(addresses);
        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(Type.class))).thenReturn(addressDtos);

        List<AddressDto> result = addressService.findAll();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findByPhoneNoSuccessTest() {

        Address a1 = new Address();
        Address a2 = new Address();

        List<Address> addresses = List.of(a1, a2);

        AddressDto d1 = new AddressDto();
        AddressDto d2 = new AddressDto();

        List<AddressDto> addressDtos = List.of(d1, d2);

        Mockito.when(addressRepository.findByPhoneNo("9999999999")).thenReturn(addresses);
        Mockito.when(modelMapper.map(Mockito.eq(addresses), Mockito.any(Type.class))).thenReturn(addressDtos);

        List<AddressDto> result = addressService.findByPhoneNo("9999999999");

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void findByPhoneNoEmptyTest() {

        Mockito.when(addressRepository.findByPhoneNo("9999999999")).thenReturn(List.of());
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        List<AddressDto> result = addressService.findByPhoneNo("9999999999");

        Assertions.assertTrue(result.isEmpty());
    }
}