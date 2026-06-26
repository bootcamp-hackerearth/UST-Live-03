package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressService;

    private AddressDto shippingDto;
    private AddressDto billingDto;

    @BeforeEach
    void setUp() {

        shippingDto = new AddressDto();
        shippingDto.setIdentifier("SHIP001");

        billingDto = new AddressDto();
        billingDto.setIdentifier("BILL001");
    }

    @Test
    void saveNewAddressesTest() {

        when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP001"))
                .thenReturn(null);

        when(addressRepository.findByIdentifierAndIsBillingTrue("BILL001"))
                .thenReturn(null);

        addressService.save(shippingDto, billingDto);

        verify(addressRepository, times(2))
                .save(any(Address.class));
    }

    @Test
    void saveExistingAddressesTest() {

        Address shipping = new Address();
        Address billing = new Address();

        when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP001"))
                .thenReturn(shipping);

        when(addressRepository.findByIdentifierAndIsBillingTrue("BILL001"))
                .thenReturn(billing);

        addressService.save(shippingDto, billingDto);

        Assertions.assertFalse(shippingDto.isSuccess());
        Assertions.assertFalse(billingDto.isSuccess());

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    void updateSuccessTest() {

        Address shipping = new Address();
        shipping.setId(1L);

        Address billing = new Address();
        billing.setId(2L);

        when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP001"))
                .thenReturn(shipping);

        when(addressRepository.findByIdentifierAndIsBillingTrue("BILL001"))
                .thenReturn(billing);

        addressService.update(shippingDto, billingDto);

        verify(addressRepository, times(2))
                .save(any(Address.class));
    }

    @Test
    void updateAddressNotFoundTest() {

        when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP001"))
                .thenReturn(null);

        when(addressRepository.findByIdentifierAndIsBillingTrue("BILL001"))
                .thenReturn(null);

        addressService.update(shippingDto, billingDto);

        Assertions.assertTrue(
                shippingDto.getMessage().contains("Shipping address not found")
        );

        Assertions.assertTrue(
                billingDto.getMessage().contains("Billing address not found")
        );

        verify(addressRepository, never())
                .save(any(Address.class));
    }

    @Test
    void deleteTest() {

        addressService.delete("ADDR001");

        verify(addressRepository)
                .deleteByIdentifier("ADDR001");
    }

    @Test
    void findAllTest() {

        Address address = new Address();
        address.setIdentifier("ADDR001");

        when(addressRepository.findAll())
                .thenReturn(List.of(address));

        List<AddressDto> response =
                addressService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierAndShippingTest() {

        Address address = new Address();
        address.setIdentifier("SHIP001");

        when(addressRepository.findByIdentifierAndIsShippingTrue("SHIP001"))
                .thenReturn(address);

        AddressDto response =
                addressService.findByIdentifierAndShipping("SHIP001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("SHIP001",
                response.getIdentifier());
    }

    @Test
    void findByIdentifierAndBillingTest() {

        Address address = new Address();
        address.setIdentifier("BILL001");

        when(addressRepository.findByIdentifierAndIsBillingTrue("BILL001"))
                .thenReturn(address);

        AddressDto response =
                addressService.findByIdentifierAndBilling("BILL001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("BILL001",
                response.getIdentifier());
    }

    @Test
    void findAllPageableTest() {

        Address address = new Address();
        address.setIdentifier("ADDR001");

        Page<Address> page =
                new PageImpl<>(List.of(address));

        when(addressRepository.findAll(any(PageRequest.class)))
                .thenReturn(page);

        List<AddressDto> response =
                addressService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.size());
    }
}