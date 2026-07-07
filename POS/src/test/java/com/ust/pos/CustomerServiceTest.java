package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;
    private AddressDto addressDto1;
    private AddressDto addressDto2;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setPhoneNo("1234567890");
        customer.setCustomerName("John Doe");
        customer.setDeleted(false);
        customer.setStatus(true);

        customerDto = new CustomerDto();
        customerDto.setPhoneNo("1234567890");
        customerDto.setCustomerName("John Doe");

        addressDto1 = new AddressDto();
        addressDto1.setAddressLine("Billing Lane");

        addressDto2 = new AddressDto();
        addressDto2.setAddressLine("Shipping Street");
    }

    @Test
    void testFindById() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);

        CustomerDto result = customerService.findById("1234567890");

        assertNotNull(result);
        assertEquals("1234567890", result.getPhoneNo());
    }

    @Test
    void testFindByIdentifierWithAddressDto_CustomerNotFound() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("1234567890");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testFindByIdentifierWithAddressDto_WithAddresses() {
        List<AddressDto> addressList = Arrays.asList(addressDto1, addressDto2);
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("1234567890")).thenReturn(addressList);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("1234567890");

        assertNotNull(result);
        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
    }

    @Test
    void testFindByIdentifierWithAddressDto_NoAddresses() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("1234567890")).thenReturn(new ArrayList<>());

        CustomerDto result = customerService.findByIdentifierWithAddressDto("1234567890");

        assertNotNull(result);
        assertNull(result.getBillingAddress());
        assertNull(result.getShippingAddress());
    }

    @Test
    void testSave_InvalidPhone() {
        customerDto.setPhoneNo("123");
        CustomerDto resultShort = customerService.save(customerDto);
        assertFalse(resultShort.isSuccess());

        customerDto.setPhoneNo(null);
        CustomerDto resultNullPhone = customerService.save(customerDto);
        assertFalse(resultNullPhone.isSuccess());

        CustomerDto resultNullDto = customerService.save(null);
        assertNull(resultNullDto);
    }

    @Test
    void testSave_CustomerAlreadyExists() {
        customer.setDeleted(false);
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_CustomerPreviouslyDeleted() {
        customer.setDeleted(true);
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_SuccessWithAddresses() {
        customerDto.setBillingAddress(addressDto1);
        customerDto.setShippingAddress(addressDto2);

        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(null);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(addressService.save(any(AddressDto.class))).thenReturn(addressDto1);

        CustomerDto result = customerService.save(customerDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(addressService, times(2)).save(any(AddressDto.class));
    }

    @Test
    void testUpdate_CustomerNotFound() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_CustomerDeleted() {
        customer.setDeleted(true);
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);

        CustomerDto result = customerService.update(customerDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_SuccessWithAddresses() {
        customerDto.setBillingAddress(addressDto1);
        customerDto.setShippingAddress(addressDto2);

        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(addressService.update(any(AddressDto.class))).thenReturn(addressDto1);

        CustomerDto result = customerService.update(customerDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(addressService, times(2)).update(any(AddressDto.class));
    }

    @Test
    void testDelete_CustomerNotFound() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(null);

        boolean result = customerService.delete("1234567890");

        assertFalse(result);
    }

    @Test
    void testDelete_Success() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(addressService.delete("1234567890")).thenReturn(true);

        boolean result = customerService.delete("1234567890");

        assertTrue(result);
        verify(addressService, times(1)).delete("1234567890");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> list = Collections.singletonList(customer);
        Page<Customer> page = new PageImpl<>(list, pageable, 1);

        when(customerRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_CustomerNotFound() {
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(null);

        CustomerDto result = customerService.toggleStatus("1234567890");

        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    @Test
    void testToggleStatus_Success() {
        customer.setStatus(true);
        when(customerRepository.findByPhoneNo("1234567890")).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerDto result = customerService.toggleStatus("1234567890");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Customer> list = Collections.singletonList(customer);
        when(customerRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(list);

        List<CustomerDto> result = customerService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }


}