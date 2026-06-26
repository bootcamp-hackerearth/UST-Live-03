package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
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

    @InjectMocks
    private CustomerServiceImpl service;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Customer> page = new PageImpl<>(List.of(new Customer()), pageable, 1);

        when(customerRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new CustomerDto()));

        WsDto<CustomerDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierTest() {
        Customer customer = new Customer();
        customer.setPhoneNo("123");

        CustomerDto dto = new CustomerDto();

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        when(addressService.findByPhoneNoAndAddressType("123", "BILLING")).thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType("123", "SHIPPING")).thenReturn(shipping);

        CustomerDto result = service.findByIdentifier("C1");

        assertNotNull(result);
        assertEquals(billing, result.getBillingAddress());
        assertEquals(shipping, result.getShippingAddress());
    }

    @Test
    void saveSuccessWithAddressesTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo("123");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        when(modelMapper.map(dto, Customer.class)).thenReturn(new Customer());

        CustomerDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(any());
    }

    @Test
    void saveSuccessWithoutAddressesTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo("123");

        when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        when(modelMapper.map(dto, Customer.class)).thenReturn(new Customer());

        CustomerDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(customerRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Customer existing = new Customer();
        existing.setDeleted(false);

        when(customerRepository.findByIdentifier("C1")).thenReturn(existing);

        CustomerDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Customer existing = new Customer();
        existing.setDeleted(true);

        when(customerRepository.findByIdentifier("C1")).thenReturn(existing);

        CustomerDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();
        customer.setPhoneNo("123");

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        service.delete("C1");

        verify(addressService).deleteByPhoneNo("123");
        assertTrue(customer.isDeleted());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Customer customer = new Customer();
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        service.toggleStatus("C1");

        assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Customer customer = new Customer();
        customer.setStatus(false);

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        service.toggleStatus("C1");

        assertTrue(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        service.toggleStatus("C1");

        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo("123");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer existing = new Customer();

        when(customerRepository.findByIdentifier("C1")).thenReturn(existing);

        CustomerDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(customerRepository, never()).save(any());
    }
}
