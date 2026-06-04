package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    AddressService addressService;
    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    void saveCustomerSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNum("111");
        dto.setUsername("test@mail.com");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Mockito.when(customerRepository.findByPhoneNum("111"))
                .thenReturn(null);

        Mockito.doAnswer(invocation -> null)
                .when(modelMapper)
                .map(Mockito.any(CustomerDto.class), Mockito.any(Customer.class));

        Mockito.when(customerRepository.save(Mockito.any(Customer.class)))
                .thenReturn(new Customer());

        Mockito.when(addressRepository.save(Mockito.any(Address.class)))
                .thenReturn(new Address());

        CustomerDto response = customerService.save(dto);

        Assertions.assertNotNull(response);
    }

    @Test
    void saveCustomerAlreadyExists() {
        Mockito.when(customerRepository.findByPhoneNum("111"))
                .thenReturn(new Customer());

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNum("111");

        CustomerDto response = customerService.save(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierSuccess() {
        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(new CustomerDto());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.BILLING_ADDRESS))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.SHIPPING_ADDRESS))
                .thenReturn(new AddressDto());

        CustomerDto response = customerService.findByIdentifier("123");

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierCustomerNotFound() {
        Mockito.when(customerRepository.findByIdentifier("999"))
                .thenReturn(null);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> customerService.findByIdentifier("999")
        );
    }

    @Test
    void changeCustomerStatusSuccess() {
        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);
        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(new CustomerDto());

        CustomerDto response =
                customerService.changeCustomerStatus("123", true);

        Assertions.assertNotNull(response);
    }

    @Test
    void changeCustomerStatusCustomerNull() {
        Mockito.when(customerRepository.findByIdentifier("999"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(null, CustomerDto.class))
                .thenReturn(null);

        CustomerDto response =
                customerService.changeCustomerStatus("999", true);

        Assertions.assertNull(response);
    }

    @Test
    void findAllCustomers() {
        Page<Customer> page = new PageImpl<>(List.of(new Customer()));

        Mockito.when(customerRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(listType)))
                .thenReturn(List.of(new CustomerDto()));

        List<CustomerDto> result =
                customerService.findAll(PageRequest.of(0, 5));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void updateCustomerSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(new Customer());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.BILLING_ADDRESS))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.SHIPPING_ADDRESS))
                .thenReturn(new AddressDto());

        CustomerDto response = customerService.update(dto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateCustomerCustomerNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");
        dto.setPhoneNum("123");

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        CustomerDto response = customerService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateCustomerBillingNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(new Customer());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.BILLING_ADDRESS))
                .thenReturn(null);

        CustomerDto response = customerService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateCustomerShippingNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(new Customer());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.BILLING_ADDRESS))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByIdentifierAndAddressType(
                        "123", CustomerServiceImpl.SHIPPING_ADDRESS))
                .thenReturn(null);

        CustomerDto response = customerService.update(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdSuccess() {
        Mockito.when(customerRepository.findById(1L))
                .thenReturn(Optional.of(new Customer()));
        Mockito.when(modelMapper.map(Mockito.any(), Mockito.eq(CustomerDto.class)))
                .thenReturn(new CustomerDto());

        CustomerDto response = customerService.findById(1L);

        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdNotFound() {
        Mockito.when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                RuntimeException.class,
                () -> customerService.findById(1L)
        );
    }

    @Test
    void deleteByIdentifier() {
        Mockito.doNothing()
                .when(customerRepository)
                .deleteByIdentifier("123");

        customerService.deleteByIdentifier("123");

        Mockito.verify(customerRepository, Mockito.times(1))
                .deleteByIdentifier("123");
    }
}