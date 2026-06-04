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
    void saveTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("9999999999");
        dto.setAddressType("HOME");

        Address address = new Address();

        Mockito.when(modelMapper.map(dto, Address.class))
                .thenReturn(address);

        Mockito.when(addressRepository.save(address))
                .thenReturn(address);

        addressService.save(dto);

        Assertions.assertTrue(dto.isSuccess());
        Assertions.assertEquals("Successfully added the address", dto.getMessage());

        Mockito.verify(addressRepository)
                .save(address);
    }

    @Test
    void findByPhoneNoAndAddressTypeTest() {
        Address address = new Address();
        address.setPhoneNo("9999999999");
        address.setAddressType("HOME");

        AddressDto dto = new AddressDto();
        dto.setPhoneNo("9999999999");
        dto.setAddressType("HOME");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(
                        "9999999999", "HOME"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response =
                addressService.findByPhoneNoAndAddressType("9999999999", "HOME");

        Assertions.assertEquals("9999999999", response.getPhoneNo());
        Assertions.assertEquals("HOME", response.getAddressType());
    }

    @Test
    void updateTest() {
        AddressDto dto = new AddressDto();
        dto.setPhoneNo("9999999999");
        dto.setAddressType("HOME");

        Address existingAddress = new Address();
        existingAddress.setPhoneNo("9999999999");
        existingAddress.setAddressType("HOME");

        Mockito.when(addressRepository.findByPhoneNoAndAddressType(
                        "9999999999", "HOME"))
                .thenReturn(existingAddress);

        Mockito.when(addressRepository.save(existingAddress))
                .thenReturn(existingAddress);

        addressService.update(dto);

        Mockito.verify(addressRepository)
                .save(existingAddress);
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(addressRepository)
                .deleteByPhoneNo("9999999999");

        addressService.delete("9999999999");

        Mockito.verify(addressRepository)
                .deleteByPhoneNo("9999999999");
    }
}