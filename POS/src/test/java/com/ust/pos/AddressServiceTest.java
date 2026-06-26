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
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccessWhenBothAddressesAreNew() {
        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");
        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address shipping = new Address();
        Address billing = new Address();

        Mockito.when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP1")).thenReturn(null);
        Mockito.when(addressRepository.findByIdentifierAndIsBillingTrue("BILL1")).thenReturn(null);
        Mockito.when(modelMapper.map(shippingDto, Address.class)).thenReturn(shipping);
        Mockito.when(modelMapper.map(billingDto, Address.class)).thenReturn(billing);

        addressService.save(shippingDto, billingDto);

        Assertions.assertTrue(shipping.getIsShipping());
        Assertions.assertTrue(billing.getIsBilling());
        Mockito.verify(addressRepository, Mockito.times(2)).save(Mockito.any(Address.class));
    }

    @Test
    void saveTestFailureWhenBillingAlreadyExists() {
        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");
        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Mockito.when(addressRepository.findByIdentifierAndIsBillingTrue("BILL1"))
                .thenReturn(new Address());
        Mockito.when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(shippingDto, Address.class))
                .thenReturn(new Address());

        addressService.save(shippingDto, billingDto);

        Assertions.assertFalse(billingDto.isSuccess());
        Assertions.assertNotNull(billingDto.getMessage());
    }

    @Test
    void updateTestSuccess() {
        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");
        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address shipping = new Address();
        shipping.setId(1L);
        Address billing = new Address();
        billing.setId(2L);

        Mockito.when(addressRepository.findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(shipping);
        Mockito.when(addressRepository.findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(billing);

        addressService.update(shippingDto, billingDto);

        Mockito.verify(addressRepository).save(shipping);
        Mockito.verify(addressRepository).save(billing);
    }

    @Test
    void updateTestFailureWhenShippingNotFound() {
        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");

        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address billing = new Address();
        billing.setId(1L);

        Mockito.when(addressRepository.findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(addressRepository.findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(billing);

        addressService.update(shippingDto, billingDto);

        Assertions.assertNotNull(shippingDto.getMessage());
    }

    @Test
    void findAllTest() {
        List<Address> addresses = List.of(new Address());
        List<AddressDto> dtos = List.of(new AddressDto());

        Type type = new TypeToken<List<AddressDto>>() {
        }.getType();

        Mockito.when(addressRepository.findByDeletedFalse()).thenReturn(addresses);
        Mockito.when(modelMapper.map(addresses, type)).thenReturn(dtos);

        Assertions.assertEquals(1, addressService.findAll().size());
    }

    @Test
    void findByIdentifierAndShippingTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        Assertions.assertNotNull(addressService.findByIdentifierAndShipping("SHIP1"));
    }

    @Test
    void findByIdentifierAndBillingTest() {
        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository.findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(address);
        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        Assertions.assertNotNull(addressService.findByIdentifierAndBilling("BILL1"));
    }

    @Test
    void deleteTest() {
        Address shipping = new Address();
        Address billing = new Address();

        Mockito.when(addressRepository.findByIdentifierAndIsShippingTrueAndDeletedFalse("ADDR1"))
                .thenReturn(shipping);
        Mockito.when(addressRepository.findByIdentifierAndIsBillingTrueAndDeletedFalse("ADDR1"))
                .thenReturn(billing);

        addressService.delete("ADDR1");

        Assertions.assertTrue(shipping.isDeleted());
        Assertions.assertTrue(billing.isDeleted());

        Mockito.verify(addressRepository).save(shipping);
        Mockito.verify(addressRepository).save(billing);
    }
}