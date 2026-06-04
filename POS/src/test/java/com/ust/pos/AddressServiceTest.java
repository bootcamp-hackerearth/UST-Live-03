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

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;


    @Test
    void save_success_whenAddressNotExists() {

        AddressDto input = new AddressDto();
        input.setIdentifier("ADDR01");

        Address entity = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR01"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(input, Address.class))
                .thenReturn(entity);
        Mockito.when(addressRepository.save(entity))
                .thenReturn(entity);

        AddressDto response = addressService.save(input);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADDR01", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess()); // default true
    }

    @Test
    void save_failure_whenAddressExists() {

        AddressDto input = new AddressDto();
        input.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01"))
                .thenReturn(new Address());

        AddressDto response = addressService.save(input);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }


    @Test
    void findByIdentifier_success() {

        Address address = new Address();
        address.setIdentifier("ADDR01");

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response = addressService.findByIdentifier("ADDR01");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADDR01", response.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {

        Mockito.when(addressRepository.findByIdentifier("ADDR01"))
                .thenReturn(null);

        AddressDto response = addressService.findByIdentifier("ADDR01");

        Assertions.assertNull(response);
    }

    @Test
    void update_success() {

        AddressDto input = new AddressDto();
        input.setIdentifier("ADDR01");

        Address existing = new Address();

        Mockito.when(addressRepository.findByIdentifier("ADDR01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(addressRepository.save(existing))
                .thenReturn(existing);

        AddressDto response = addressService.update(input);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADDR01", response.getIdentifier());
    }

    @Test
    void update_failure_whenNotFound() {

        AddressDto input = new AddressDto();
        input.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findByIdentifier("ADDR01"))
                .thenReturn(null);

        AddressDto response = addressService.update(input);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(addressRepository).deleteByIdentifier("ADDR01");

        addressService.delete("ADDR01");

        Mockito.verify(addressRepository)
                .deleteByIdentifier("ADDR01");
    }

    @Test
    void findAll_success() {

        Address address = new Address();
        address.setIdentifier("ADDR01");

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findAll())
                .thenReturn(List.of(address));
        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(address)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<AddressDto> response = addressService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllByPhoneNumber_success() {

        Address address = new Address();
        address.setIdentifier("ADDR01");

        AddressDto dto = new AddressDto();
        dto.setIdentifier("ADDR01");

        Mockito.when(addressRepository.findAllByPhoneNo("9999999999"))
                .thenReturn(List.of(address));

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(address)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<AddressDto> response =
                addressService.findAllByPhoneNumber("9999999999");

        Assertions.assertEquals(1, response.size());
    }
}