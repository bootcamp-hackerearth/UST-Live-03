package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
        customer.setStatus(true);
        customer.setIsDeleted(false);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findByPhoneNoAndAddressType(9876543210L, "billingAddress")).thenReturn(customerDto.getBillingAddress());
        when(addressService.findByPhoneNoAndAddressType(9876543210L, "shippingAddress")).thenReturn(customerDto.getShippingAddress());

        CustomerDto result = customerService.findByIdentifier("CUST123");

        assertNotNull(result);
        assertEquals("CUST123", result.getIdentifier());
        verify(addressService).findByPhoneNoAndAddressType(9876543210L, "billingAddress");
        verify(addressService).findByPhoneNoAndAddressType(9876543210L, "shippingAddress");
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
    void testSave_WhenDeletedCustomerAlreadyExists() {
        customer.setIsDeleted(true);
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("was deleted"));
    }

    @Test
    void testSave_WhenPhoneAlreadyExists() {
        Customer existingCustomer = new Customer();
        existingCustomer.setPhoneNo(9876543210L);
        existingCustomer.setIsDeleted(false);

        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(customerRepository.findByPhoneNo(9876543210L)).thenReturn(existingCustomer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testSave_WhenDeletedPhoneAlreadyExists() {
        Customer existingCustomer = new Customer();
        existingCustomer.setPhoneNo(9876543210L);
        existingCustomer.setIsDeleted(true);

        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(customerRepository.findByPhoneNo(9876543210L)).thenReturn(existingCustomer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted customer"));
    }

    @Test
    void testSave_NewCustomer() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(customerRepository.findByPhoneNo(9876543210L)).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        verify(addressService).save(customerDto.getBillingAddress());
        verify(addressService).save(customerDto.getShippingAddress());
        verify(customerRepository).save(customer);

        assertNotNull(result);
        assertFalse(customer.getIsDeleted());
    }

    @Test
    void testSave_NewCustomerNullAddresses() {
        customerDto.setBillingAddress(null);
        customerDto.setShippingAddress(null);

        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(customerRepository.findByPhoneNo(9876543210L)).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        verify(addressService, never()).save(any());
        verify(customerRepository).save(customer);

        assertNotNull(result);
        assertFalse(customer.getIsDeleted());
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
        customerDto.setPhoneNo(1234567890L);
        customer.setPhoneNo(1234567890L);

        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);
        when(addressService.findByPhoneNoAndAddressType(1234567890L, "billingAddress")).thenReturn(customerDto.getBillingAddress());
        when(addressService.findByPhoneNoAndAddressType(1234567890L, "shippingAddress")).thenReturn(customerDto.getShippingAddress());

        CustomerDto result = customerService.update(customerDto);

        verify(addressService).save(customerDto.getBillingAddress());
        verify(addressService).save(customerDto.getShippingAddress());
        verify(modelMapper).map(customerDto, customer);
        verify(customerRepository).save(customer);

        assertNotNull(result);
    }

    @Test
    void testDelete() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        CustomerDto result = customerService.delete("CUST123", 9876543210L);

        assertTrue(result.isSuccess());
        assertEquals("Customer deleted successfully", result.getMessage());
        assertTrue(customer.getIsDeleted());
        assertFalse(customer.getStatus());

        verify(customerRepository).save(customer);
        verify(addressService).softDeleteByPhone(9876543210L);
    }

    @Test
    void testDelete_NotFound() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);

        CustomerDto result = customerService.delete("CUST123", 9876543210L);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer));

        when(customerRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(customerPage);

        when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(List.of(customerDto));

        PaginatedResponseDto<CustomerDto> result = customerService.findAll(pageable);

        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllActiveTest() {
        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);

        Mockito.when(customerRepository.findByStatusAndIsDeleted(true, false)).thenReturn(customers);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(customerDtos);

        List<CustomerDto> response = customerService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTrueTest() {
        Mockito.when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        customerService.changeStatus("CUST123", true);

        Assertions.assertTrue(customer.getStatus());
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void changeStatusFalseTest() {
        Mockito.when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        customerService.changeStatus("CUST123", false);

        Assertions.assertFalse(customer.getStatus());
        Mockito.verify(customerRepository).save(customer);
    }
}