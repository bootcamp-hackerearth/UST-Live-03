package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.AddressRepository;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @Test
    void findByIdentifierSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = customerService.findByIdentifier("C1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifier("C1");

        Assertions.assertNull(result);
    }

    @Test
    void saveTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(123L);

        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(customer);

        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        Customer customer = new Customer();
        customer.setPhoneNo(123L);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(123L);

        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);
        Mockito.when(addressService.findByPhoneNoAndAddressType(123L, "billingAddress")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneNoAndAddressType(123L, "shippingAddress")).thenReturn(shipping);

        CustomerDto result = customerService.update(dto);

        verify(addressService).update(billing);
        verify(addressService).update(shipping);
        verify(modelMapper).map(dto, customer);
        verify(customerRepository).save(customer);

        Assertions.assertNotNull(result.getBillingAddress());
        Assertions.assertNotNull(result.getShippingAddress());
    }

    @Test
    void updateTestFailure() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto result = customerService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");
        customer.setPhoneNo(123L);

        Mockito.when(customerRepository.findByIdentifier("C1"))
                .thenReturn(customer);

        customerService.delete("C1");

        verify(customerRepository).findByIdentifier("C1");
        verify(customerRepository).deleteByIdentifier("C1");
        verify(addressRepository).deleteByPhoneNo(123L);
    }

    @Test
    void findAllTest() {
        List<Customer> customers = Arrays.asList(new Customer(), new Customer());

        List<CustomerDto> dtos = Arrays.asList(new CustomerDto(), new CustomerDto());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customerPage = new PageImpl<>(customers, pageable, customers.size());

        Mockito.when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(dtos);

        List<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        Mockito.verify(customerRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(customers), Mockito.any(Type.class));
    }
}