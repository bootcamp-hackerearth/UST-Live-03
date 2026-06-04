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

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_insert_success() {
        AddressDto dto = validDto();
        Mockito.when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing"))
                .thenReturn(null);
        Address mappedEntity = new Address();
        Mockito.when(modelMapper.map(dto, Address.class)).thenReturn(mappedEntity);
        addressService.save(dto);
        Assertions.assertTrue(dto.isSuccess());
        Assertions.assertEquals("Address saved successfully", dto.getMessage());
        Mockito.verify(modelMapper).map(dto, Address.class);
        Mockito.verify(addressRepository).save(mappedEntity);
    }

    @Test
    void save_update_success() {
        AddressDto dto = validDto();
        Address existing = new Address();

        Mockito.when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing"))
                .thenReturn(existing);
        addressService.save(dto);
        Assertions.assertTrue(dto.isSuccess());
        Assertions.assertEquals("Address saved successfully", dto.getMessage());
        Assertions.assertEquals("Street 1", dto.getAddressLine());
        Mockito.verify(addressRepository).save(existing);
    }

    @Test
    void save_validation_failure() {
        AddressDto dto = new AddressDto();
        addressService.save(dto);
        Assertions.assertFalse(dto.isSuccess());
        Assertions.assertEquals("All address fields are required", dto.getMessage());
        Mockito.verifyNoInteractions(addressRepository);
        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    void find_success() {
        Address entity = new Address();
        AddressDto mapped = new AddressDto();
        Mockito.when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing"))
                .thenReturn(entity);
        Mockito.when(modelMapper.map(entity, AddressDto.class)).thenReturn(mapped);

        AddressDto result = addressService.findByPhoneNoAndAddressType("9876543210", "billing");
        Assertions.assertNotNull(result);
        Assertions.assertSame(mapped, result);
        Mockito.verify(addressRepository).findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing");
    }

    @Test
    void find_not_found() {
        Mockito.when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing"))
                .thenReturn(null);
        AddressDto result = addressService.findByPhoneNoAndAddressType("9876543210", "billing");
        Assertions.assertNull(result);
    }

    @Test
    void delete_success() {
        addressService.delete("9876543210");
        Mockito.verify(addressRepository).deleteByPhoneNo("9876543210");
    }

    private AddressDto validDto() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("9876543210");
        dto.setAddressType("billing");
        dto.setAddressLine("Street 1");
        dto.setCity("Chennai");
        dto.setState("TN");
        dto.setZip("600001");
        dto.setCountry("India");
        return dto;
    }
}