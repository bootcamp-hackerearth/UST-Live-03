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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock(lenient = true)
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
        assertNull(customerService.findByIdentifier("CUST123"));
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

        assertEquals(9876543210L, customerDto.getBillingAddress().getPhoneNo());
        assertEquals(9876543210L, customerDto.getShippingAddress().getPhoneNo());
        verify(addressService, times(2)).save(any());
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

        assertEquals(9876543210L, customerDto.getBillingAddress().getPhoneNo());
        assertEquals(9876543210L, customerDto.getShippingAddress().getPhoneNo());
        verify(addressService, times(2)).update(any());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        customerService.delete("CUST123");

        verify(addressService).deleteByPhoneNo(9876543210L);
        verify(customerRepository).deleteByIdentifier("CUST123");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Customer> customers = List.of(customer);
        Page<Customer> page = new PageImpl<>(customers);

        List<CustomerDto> dtoList = List.of(customerDto);

        when(customerRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(customers),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(customerRepository).findAll(pageable);
    }

    @Test
    void testToggleStatus_CustomerExists() {
        customer.setStatus(true);
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        customerService.toggleStatus("CUST123");

        assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void testToggleStatus_CustomerNotFound() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);

        customerService.toggleStatus("CUST123");

        verify(customerRepository, never()).save(any());
    }
}