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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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
    void save_insert_success() {
        AddressDto dto = validDto();

        when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc(
                dto.getPhoneNo(), dto.getAddressType()))
                .thenReturn(null);

        Address mappedEntity = new Address();
        when(modelMapper.map(dto, Address.class)).thenReturn(mappedEntity);

        addressService.save(dto);

        Assertions.assertTrue(dto.isSuccess());
        Assertions.assertEquals("Address saved successfully", dto.getMessage());

        verify(modelMapper).map(dto, Address.class);
        verify(addressRepository).save(mappedEntity);
    }

    @Test
    void save_update_success() {
        AddressDto dto = validDto();

        Address existing = new Address();
        existing.setAddressLine("Old Address");

        when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc(
                dto.getPhoneNo(), dto.getAddressType()))
                .thenReturn(existing);

        addressService.save(dto);

        Assertions.assertTrue(dto.isSuccess());
        Assertions.assertEquals("Address saved successfully", dto.getMessage());

        Assertions.assertEquals("Street 1", existing.getAddressLine());
        Assertions.assertEquals("Chennai", existing.getCity());
        Assertions.assertEquals("TN", existing.getState());
        Assertions.assertEquals("600001", existing.getZip());
        Assertions.assertEquals("India", existing.getCountry());

        verify(addressRepository).save(existing);
    }

    @Test
    void save_validation_failure() {
        AddressDto dto = new AddressDto();

        addressService.save(dto);

        Assertions.assertFalse(dto.isSuccess());
        Assertions.assertEquals("All address fields are required", dto.getMessage());

        verifyNoInteractions(addressRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void find_success() {
        Address entity = new Address();
        AddressDto mapped = new AddressDto();

        when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc(
                "9876543210", "billing"))
                .thenReturn(entity);

        when(modelMapper.map(entity, AddressDto.class)).thenReturn(mapped);

        AddressDto result = addressService.findByPhoneNoAndAddressType(
                "9876543210", "billing");

        Assertions.assertNotNull(result);
        Assertions.assertSame(mapped, result);

        verify(addressRepository)
                .findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing");
    }

    @Test
    void find_not_found() {
        when(addressRepository.findTopByPhoneNoAndAddressTypeOrderByIdDesc(
                "9876543210", "billing"))
                .thenReturn(null);

        AddressDto result = addressService.findByPhoneNoAndAddressType(
                "9876543210", "billing");

        Assertions.assertNull(result);

        verify(addressRepository)
                .findTopByPhoneNoAndAddressTypeOrderByIdDesc("9876543210", "billing");
        verifyNoInteractions(modelMapper);
    }

    @Test
    void delete_success() {
        addressService.delete("9876543210");

        verify(addressRepository).deleteByPhoneNo("9876543210");
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