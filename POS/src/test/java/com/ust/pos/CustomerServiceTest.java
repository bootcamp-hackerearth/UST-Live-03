package com.ust.pos;

import com.ust.pos.address.service.AddressService;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;
    private AddressDto billing;
    private AddressDto shipping;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdentifier("12345");
        customer.setPhoneNo("12345");
        customer.setStatus(true);

        billing = new AddressDto();
        shipping = new AddressDto();

        customerDto = new CustomerDto();
        customerDto.setPhoneNo("12345");
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);
    }

    @Test
    void findByIdentifier_NotFound() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifier("12345");

        assertNull(result);
    }

    @Test
    void findByIdentifier_Found() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findByPhoneNoAndAddressType("12345", "billing")).thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType("12345", "shipping")).thenReturn(shipping);

        CustomerDto result = customerService.findByIdentifier("12345");

        assertNotNull(result);
        assertEquals(billing, result.getBillingAddress());
        assertEquals(shipping, result.getShippingAddress());
    }

    @Test
    void save_AlreadyExists() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void save_NewCustomer() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        verify(addressService, times(1)).save(billing);
        verify(addressService, times(1)).save(shipping);
        verify(customerRepository, times(1)).save(customer);

        assertEquals("12345", customer.getIdentifier());
    }

    @Test
    void update_NotFound() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void update_Success() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);

        when(addressService.findByPhoneNoAndAddressType(anyString(), anyString()))
                .thenReturn(billing)
                .thenReturn(shipping);

        CustomerDto result = customerService.update(customerDto);

        verify(addressService, times(1)).update(billing);
        verify(addressService, times(1)).update(shipping);
        verify(customerRepository, times(1)).save(customer);

        assertNotNull(result);
    }

    @Test
    void updateStatus_NotFound() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);

        CustomerDto result = customerService.updateStatus("12345", true);

        assertFalse(result.isSuccess());
        assertEquals("Product not found", result.getMessage());
    }

    @Test
    void updateStatus_Success() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);

        CustomerDto result = customerService.updateStatus("12345", false);

        assertTrue(result.isSuccess());
        assertEquals("Status updated successfully", result.getMessage());
        assertFalse(customer.isStatus());
    }

    @Test
    void deleteCustomer() {
        doNothing().when(customerRepository).deleteByIdentifier("12345");
        doNothing().when(addressService).delete("12345");

        customerService.delete("12345");

        verify(customerRepository).deleteByIdentifier("12345");
        verify(addressService).delete("12345");
    }

    @Test
    void findAllWithPageableTest() {

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> customerPage = new PageImpl<>(customers);

        when(customerRepository.findAll(pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(
                eq(customers),
                any(Type.class)
        )).thenReturn(customerDtos);

        List<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CUST1", result.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);

        when(customerRepository.findAll())
                .thenReturn(customers);

        when(modelMapper.map(
                eq(customers),
                any(Type.class)
        )).thenReturn(customerDtos);

        List<CustomerDto> result = customerService.findAll(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CUST1", result.get(0).getIdentifier());
    }
}