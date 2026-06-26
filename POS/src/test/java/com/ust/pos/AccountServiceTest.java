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

// ================= SAVE =================

    @Test
    void saveTest_Success_WhenBothAddressesAreNew() {

        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");

        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address shipping = new Address();
        Address billing = new Address();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(shippingDto, Address.class))
                .thenReturn(shipping);

        Mockito.when(modelMapper.map(billingDto, Address.class))
                .thenReturn(billing);

        addressService.save(shippingDto, billingDto);

        Mockito.verify(addressRepository).save(shipping);
        Mockito.verify(addressRepository).save(billing);

        Assertions.assertFalse(shipping.getDeleted());
        Assertions.assertFalse(billing.getDeleted());
        Assertions.assertTrue(shipping.getIsShipping());
        Assertions.assertTrue(billing.getIsBilling());
    }

    @Test
    void saveTest_Failure_WhenBillingAlreadyExists() {

        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");

        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address existingBilling = new Address();
        Address shipping = new Address();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(existingBilling);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(shippingDto, Address.class))
                .thenReturn(shipping);

        addressService.save(shippingDto, billingDto);

        Assertions.assertFalse(billingDto.isSuccess());
        Assertions.assertNotNull(billingDto.getMessage());

        Mockito.verify(addressRepository).save(shipping);
    }

// ================= UPDATE =================

    @Test
    void updateTest_Success() {

        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");

        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address existingShipping = new Address();
        existingShipping.setId(1L);

        Address existingBilling = new Address();
        existingBilling.setId(2L);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(existingShipping);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(existingBilling);

        addressService.update(shippingDto, billingDto);

        Mockito.verify(addressRepository).save(existingShipping);
        Mockito.verify(addressRepository).save(existingBilling);
    }

    @Test
    void updateTest_Failure_WhenShippingNotFound() {

        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP1");

        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("BILL1");

        Address existingBilling = new Address();
        existingBilling.setId(10L);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(existingBilling);

        addressService.update(shippingDto, billingDto);

        Assertions.assertNotNull(shippingDto.getMessage());
    }

// ================= FIND ALL =================

    @Test
    void findAllTest() {

        List<Address> addresses = List.of(new Address());
        List<AddressDto> addressDtos = List.of(new AddressDto());

        Type listType =
                new TypeToken<List<AddressDto>>() {}.getType();

        Mockito.when(addressRepository.findByDeletedFalse())
                .thenReturn(addresses);

        Mockito.when(modelMapper.map(addresses, listType))
                .thenReturn(addressDtos);

        List<AddressDto> response =
                addressService.findAll();

        Assertions.assertEquals(1, response.size());
    }

// ================= FIND SHIPPING =================

    @Test
    void findByIdentifierAndShippingTest() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("SHIP1"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response =
                addressService.findByIdentifierAndShipping("SHIP1");

        Assertions.assertNotNull(response);
    }

// ================= FIND BILLING =================

    @Test
    void findByIdentifierAndBillingTest() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("BILL1"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto response =
                addressService.findByIdentifierAndBilling("BILL1");

        Assertions.assertNotNull(response);
    }

// ================= DELETE =================

    @Test
    void deleteTest() {

        Address billing = new Address();
        billing.setDeleted(false);

        Address shipping = new Address();
        shipping.setDeleted(false);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("ADDR1"))
                .thenReturn(billing);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("ADDR1"))
                .thenReturn(shipping);

        addressService.delete("ADDR1");

        Assertions.assertTrue(billing.getDeleted());
        Assertions.assertTrue(shipping.getDeleted());

        Mockito.verify(addressRepository).save(billing);
        Mockito.verify(addressRepository).save(shipping);
    }

    @Test
    void deleteTest_NotFound() {

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndDeletedFalse("ADDR1"))
                .thenReturn(null);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndDeletedFalse("ADDR1"))
                .thenReturn(null);

        addressService.delete("ADDR1");

        Mockito.verify(addressRepository,
                Mockito.never()).save(Mockito.any());
    }

}