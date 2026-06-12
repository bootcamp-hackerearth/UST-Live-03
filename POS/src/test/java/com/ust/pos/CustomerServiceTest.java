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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private AddressDto billingAddress;
    private AddressDto shippingAddress;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdentifier("C001");
        customer.setPhoneNo("9876543210");
        customer.setStatus(true);

        billingAddress = new AddressDto();
        billingAddress.setAddressType("Billing");

        shippingAddress = new AddressDto();
        shippingAddress.setAddressType("Shipping");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("C001");
        customerDto.setPhoneNo("9876543210");
        customerDto.setBillingAddress(billingAddress);
        customerDto.setShippingAddress(shippingAddress);
    }


    @Test
    void findById_shouldReturnCustomerDto() {
        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findById("C001");

        assertNotNull(result);
        assertEquals("9876543210", result.getPhoneNo());
    }


    @Test
    void findByIdentifierWithAddressDto_bothAddresses() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(List.of(billingAddress, shippingAddress));

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
    }

    @Test
    void findByIdentifierWithAddressDto_nullAddressList() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
    }

    @Test
    void findByIdentifierWithAddressDto_emptyList_branchCoverage() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        List<AddressDto> list = new ArrayList<>();
        list.add(billingAddress);
        List<AddressDto> spyList = Mockito.spy(list);
        when(spyList.isEmpty()).thenReturn(true);

        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(spyList);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result.getBillingAddress());
    }

    @Test
    void findByIdentifierWithAddressDto_singleAddress() {
        customerDto.setShippingAddress(null);

        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(List.of(billingAddress));

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result.getBillingAddress());
        assertNull(result.getShippingAddress());
    }


    @Test
    void save_shouldPersistCustomerAndAddresses() {
        when(customerRepository.findById("C001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(any(AddressDto.class), eq(AddressDto.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerDto result = customerService.save(customerDto);

        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
        assertEquals("C001", result.getIdentifier());
    }

    @Test
    void save_shouldFail_whenCustomerExists() {
        when(customerRepository.findById("C001")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        verify(customerRepository, never()).save(any());
    }


    @Test
    void update_shouldUpdateCustomerAndAddresses() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(List.of(billingAddress, shippingAddress));

        CustomerDto result = customerService.update(customerDto);

        verify(modelMapper).map(customerDto, customer);
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).update(any(AddressDto.class));
        assertEquals("9876543210", result.getPhoneNo());
    }

    @Test
    void update_shouldFail_whenCustomerNotFound() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }


    @Test
    void delete_shouldDeleteCustomerAndAddresses() {
        doNothing().when(customerRepository).deleteByPhoneNo("9876543210");
        when(addressService.delete("9876543210")).thenReturn(true);

        boolean result = customerService.delete("9876543210");

        verify(customerRepository).deleteByPhoneNo("9876543210");
        verify(addressService).delete("9876543210");
        assertTrue(result);
    }


    @Test
    void findAllTest() {
        Page<Customer> page = new PageImpl<>(List.of(customer), PageRequest.of(0, 2), 1);

        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findAll(PageRequest.of(0, 2)).getDtoList();

        assertEquals(1, result.size());
    }


    @Test
    void toggleStatus_shouldToggleCustomerStatus() {
        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.toggleStatus("C001");

        assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }


    @Test
    void findIfTrue_shouldReturnActiveCustomers() {
        when(customerRepository.findByStatusIsTrue()).thenReturn(List.of(customer));
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findIfTrue();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isStatus());
    }
}