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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    // ---------------- SAVE ----------------

    @Test
    void save_ShouldSaveBothAddresses_WhenNotExists() {

        AddressDto shipping = new AddressDto();
        shipping.setIdentifier("SHIP1");

        AddressDto billing = new AddressDto();
        billing.setIdentifier("BILL1");

        Address shippingEntity = new Address();
        Address billingEntity = new Address();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("BILL1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(shipping, Address.class))
                .thenReturn(shippingEntity);

        Mockito.when(modelMapper.map(billing, Address.class))
                .thenReturn(billingEntity);

        addressService.save(shipping, billing);

        Mockito.verify(addressRepository).save(shippingEntity);
        Mockito.verify(addressRepository).save(billingEntity);
    }

    @Test
    void save_ShouldNotSaveBilling_WhenAlreadyExists() {

        AddressDto shipping = new AddressDto();
        shipping.setIdentifier("SHIP1");

        AddressDto billing = new AddressDto();
        billing.setIdentifier("BILL1");

        Address existingBilling = new Address();
        Address shippingEntity = new Address();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("BILL1"))
                .thenReturn(existingBilling);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(shipping, Address.class))
                .thenReturn(shippingEntity);

        addressService.save(shipping, billing);

        Assertions.assertFalse(billing.isSuccess());
        Assertions.assertTrue(billing.getMessage().contains("already exists"));

        Mockito.verify(addressRepository).save(shippingEntity);
        Mockito.verify(addressRepository, Mockito.never())
                .save(existingBilling);
    }

    // ---------------- UPDATE ----------------

    @Test
    void update_ShouldUpdateBothAddresses() {

        AddressDto shipping = new AddressDto();
        shipping.setIdentifier("SHIP1");

        AddressDto billing = new AddressDto();
        billing.setIdentifier("BILL1");

        Address shippingEntity = new Address();
        shippingEntity.setId(1L);

        Address billingEntity = new Address();
        billingEntity.setId(2L);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("SHIP1"))
                .thenReturn(shippingEntity);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("BILL1"))
                .thenReturn(billingEntity);

        addressService.update(shipping, billing);

        Mockito.verify(addressRepository).save(shippingEntity);
        Mockito.verify(addressRepository).save(billingEntity);
    }

    @Test
    void update_ShouldReturnMessage_WhenShippingNotFound() {

        AddressDto shipping = new AddressDto();
        shipping.setIdentifier("SHIP1");

        AddressDto billing = new AddressDto();
        billing.setIdentifier("BILL1");

        Address billingEntity = new Address();
        billingEntity.setId(2L);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("SHIP1"))
                .thenReturn(null);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("BILL1"))
                .thenReturn(billingEntity);

        addressService.update(shipping, billing);

        Assertions.assertTrue(
                shipping.getMessage().contains("Shipping address not found"));

        Mockito.verify(addressRepository).save(billingEntity);
    }

    // ---------------- DELETE ----------------

    @Test
    void delete_ShouldSoftDeleteShippingAndBilling() {

        Address shipping = new Address();
        Address billing = new Address();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("ADDR1"))
                .thenReturn(shipping);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("ADDR1"))
                .thenReturn(billing);

        addressService.delete("ADDR1");

        Assertions.assertTrue(shipping.isDelete());
        Assertions.assertTrue(billing.isDelete());

        Mockito.verify(addressRepository).save(shipping);
        Mockito.verify(addressRepository).save(billing);
    }

    @Test
    void delete_ShouldDoNothing_WhenAddressNotFound() {

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("ADDR1"))
                .thenReturn(null);

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("ADDR1"))
                .thenReturn(null);

        addressService.delete("ADDR1");

        Mockito.verify(addressRepository, Mockito.never())
                .save(Mockito.any(Address.class));
    }

    // ---------------- FIND ALL ----------------

    @Test
    void findAll_ShouldReturnMappedDtos() {

        List<Address> addresses = List.of(new Address());
        List<AddressDto> dtos = List.of(new AddressDto());

        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();

        Mockito.when(addressRepository.findAll())
                .thenReturn(addresses);

        Mockito.when(modelMapper.map(addresses, listType))
                .thenReturn(dtos);

        List<AddressDto> response = addressService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    // ---------------- FIND SHIPPING ----------------

    @Test
    void findShipping_ShouldReturnDto() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("SHIP1"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto result =
                addressService.findByIdentifierAndShipping("SHIP1");

        Assertions.assertNotNull(result);
    }

    @Test
    void findShipping_ShouldReturnEmptyDto_WhenNotFound() {

        Mockito.when(addressRepository
                        .findByIdentifierAndIsShippingTrueAndIsDeleteFalse("SHIP1"))
                .thenReturn(null);

        AddressDto result =
                addressService.findByIdentifierAndShipping("SHIP1");

        Assertions.assertNotNull(result);
    }

    // ---------------- FIND BILLING ----------------

    @Test
    void findBilling_ShouldReturnDto() {

        Address address = new Address();
        AddressDto dto = new AddressDto();

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("BILL1"))
                .thenReturn(address);

        Mockito.when(modelMapper.map(address, AddressDto.class))
                .thenReturn(dto);

        AddressDto result =
                addressService.findByIdentifierAndBilling("BILL1");

        Assertions.assertNotNull(result);
    }

    @Test
    void findBilling_ShouldReturnEmptyDto_WhenNotFound() {

        Mockito.when(addressRepository
                        .findByIdentifierAndIsBillingTrueAndIsDeleteFalse("BILL1"))
                .thenReturn(null);

        AddressDto result =
                addressService.findByIdentifierAndBilling("BILL1");

        Assertions.assertNotNull(result);
    }

    // ---------------- PAGINATION ----------------

    @Test
    void findAll_WithPagination_ShouldReturnDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Address> addresses = List.of(new Address());
        Page<Address> page = new PageImpl<>(addresses);

        List<AddressDto> dtos = List.of(new AddressDto());

        Type listType = new TypeToken<List<AddressDto>>() {
        }.getType();

        Mockito.when(addressRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(addresses, listType))
                .thenReturn(dtos);

        List<AddressDto> result = addressService.findAll(pageable);

        Assertions.assertEquals(1, result.size());

        Mockito.verify(addressRepository).findAll(pageable);
    }
}