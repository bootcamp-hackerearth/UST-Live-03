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

import java.lang.reflect.Type;
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
    private AddressDto billing;
    private AddressDto shipping;

    @BeforeEach
    void setup() {

        customer = new Customer();
        customer.setIdentifier("cust1");
        customer.setPhoneNo(123456L);
        customer.setStatus(true);

        billing = new AddressDto();
        shipping = new AddressDto();

        customerDto = new CustomerDto();
        customerDto.setIdentifier("cust1");
        customerDto.setPhoneNo(123456L);
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);
    }

    @Test
    void testFindByIdentifier_found() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findByIdentifier("cust1");
        assertNotNull(result);
        assertEquals("cust1", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_notFound() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(null);
        CustomerDto result = customerService.findByIdentifier("cust1");
        assertNull(result);
    }

    @Test
    void testSave_success() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        CustomerDto result = customerService.save(customerDto);

        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(customer);
        assertTrue(result.isSuccess());
    }

    @Test
    void testSave_duplicate() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(customer);
        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testUpdate_success() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(customer);
        when(addressService.findByPhoneNoAndAddressType(anyLong(), eq("billingAddress")))
                .thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType(anyLong(), eq("shippingAddress")))
                .thenReturn(shipping);

        CustomerDto result = customerService.update(customerDto);

        verify(addressService).update(billing);
        verify(addressService).update(shipping);
        verify(customerRepository).save(customer);

        assertNotNull(result);
    }

    @Test
    void testUpdate_notFound() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(null);
        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete() {

        customerService.delete("cust1", 123456L);
        verify(customerRepository).deleteByIdentifier("cust1");
        verify(addressService).deleteByPhoneNo(123456L);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> customers = List.of(new Customer());
        Page<Customer> page = new PageImpl<>(customers);
        List<CustomerDto> dtos = List.of(new CustomerDto());

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(
                eq(customers),
                any(Type.class)
        )).thenReturn(dtos);

        List<CustomerDto> result = customerService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(customerRepository).findAll(pageable);
    }

    @Test
    void testToggleStatus_notFound() {

        when(customerRepository.findByIdentifier("cust1")).thenReturn(null);
        customerService.toggleStatus("cust1");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testToggleStatus_falseToTrue() {

        customer.setStatus(false);
        when(customerRepository.findByIdentifier("cust1"))
                .thenReturn(customer);
        customerService.toggleStatus("cust1");

        assertTrue(customer.isStatus());
    }


    @Test
    void testFindActiveCustomer() {

        when(customerRepository.findByStatus(true)).thenReturn(List.of(customer));
        List<Customer> result = customerService.findActiveCustomer();
        assertEquals(1, result.size());
    }
}
