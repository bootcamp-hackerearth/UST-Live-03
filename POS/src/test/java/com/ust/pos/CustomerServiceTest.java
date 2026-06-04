package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST123");
        customerDto.setPhoneNo(9876543210L);
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        customer = new Customer();
        customer.setIdentifier("CUST123");
        customer.setPhoneNo(9876543210L);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findByIdentifier("CUST123");

        assertNotNull(result);
        assertEquals("CUST123", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifier("CUST123");

        assertNull(result);
    }

    @Test
    void testSave_WhenCustomerAlreadyExists() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testSave_NewCustomer() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        verify(addressService).save(customerDto.getBillingAddress());
        verify(addressService).save(customerDto.getShippingAddress());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void testUpdate_CustomerNotFound() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_CustomerFound() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        when(addressService.findByPhoneNoAndAddressType(
                anyLong(), eq("billingAddress"))).thenReturn(customerDto.getBillingAddress());
        when(addressService.findByPhoneNoAndAddressType(
                anyLong(), eq("shippingAddress"))).thenReturn(customerDto.getShippingAddress());

        CustomerDto result = customerService.update(customerDto);

        verify(addressService).update(customerDto.getBillingAddress());
        verify(addressService).update(customerDto.getShippingAddress());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        doNothing().when(customerRepository).deleteByIdentifier("CUST123");
        doNothing().when(addressService).deleteByPhone(9876543210L);

        customerService.delete("CUST123", 9876543210L);

        verify(customerRepository).deleteByIdentifier("CUST123");
        verify(addressService).deleteByPhone(9876543210L);
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 50);

        List<Customer> customerList =
                Collections.singletonList(customer);

        List<CustomerDto> customerDtoList =
                Collections.singletonList(customerDto);

        Page<Customer> customerPage =
                new PageImpl<>(customerList, pageable, customerList.size());

        Type listType =
                new TypeToken<List<CustomerDto>>() {
                }.getType();

        when(customerRepository.findAll(pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(customerPage.getContent(), listType))
                .thenReturn(customerDtoList);

        List<CustomerDto> result =
                customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(customerRepository, times(1))
                .findAll(pageable);

        verify(modelMapper, times(1))
                .map(customerPage.getContent(), listType);
    }
}