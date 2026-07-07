package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    @Spy
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @Test
    void findByIdentifierTest() {

        Customer customer = new Customer();
        customer.setIdentifier("123");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        when(customerRepository.findByIdentifier("123")).thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = customerService.findByIdentifier("123");

        assertNotNull(result);
        assertEquals("123", result.getIdentifier());

        verify(customerRepository).findByIdentifier("123");

    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(customerRepository.findByIdentifier("123")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> customerService.findByIdentifier("123"));

    }

    @Test
    void findByIdentifierWithAddressDtoTest() {

        Customer customer = new Customer();

        CustomerDto dto = new CustomerDto();

        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");

        when(customerRepository.findByIdentifier("123")).thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        when(addressService.findAllByPhoneNo("123")).thenReturn(List.of(billing, shipping));

        CustomerDto result = customerService.findByIdentifierWithAddressDto("123");

        assertNotNull(result.getBillingAddress());

        assertNotNull(result.getShippingAddress());

    }

    @Test
    void findByIdentifierWithEmptyAddressTest() {

        Customer customer = new Customer();

        CustomerDto dto = new CustomerDto();

        when(customerRepository.findByIdentifier("123")).thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        when(addressService.findAllByPhoneNo("123")).thenReturn(List.of());

        CustomerDto result = customerService.findByIdentifierWithAddressDto("123");

        assertNotNull(result.getBillingAddress());

        assertNotNull(result.getShippingAddress());

    }

    @Test
    void saveTest() {

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        AddressDto billing = new AddressDto();

        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("123")).thenReturn(null);

        when(modelMapper.map(dto, Customer.class)).thenReturn(customer);

        when(modelMapper.map(billing, AddressDto.class)).thenReturn(new AddressDto());

        when(modelMapper.map(shipping, AddressDto.class)).thenReturn(new AddressDto());

        CustomerDto result = customerService.save(dto);

        assertTrue(result.isSuccess());

        assertEquals("Customer saved successfully", result.getMessage());

        verify(customerRepository).save(customer);

        verify(addressService, times(2)).save(any());

    }

    @Test
    void saveWithoutAddressesTest() {

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("123")).thenReturn(null);

        when(modelMapper.map(dto, Customer.class)).thenReturn(customer);

        customerService.save(dto);

        verify(customerRepository).save(customer);

        verify(addressService, never()).save(any());

    }

    @Test
    void saveAlreadyExistsTest() {

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        Customer existing = new Customer();

        when(customerRepository.findByIdentifier("123")).thenReturn(existing);

        CustomerDto result = customerService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Customer with identifier - 123 already exists", result.getMessage());

    }

    @Test
    void saveSoftDeletedTest() {

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        Customer existing = new Customer();

        existing.setDeleted(true);

        when(customerRepository.findByIdentifier("123")).thenReturn(existing);

        CustomerDto result = customerService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Customer with identifier - 123 has been soft deleted.(Rollback by changing status", result.getMessage());

    }

    @Test
    void updateTest() {

        Customer existing = new Customer();

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        AddressDto billing = new AddressDto();

        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        AddressDto existingBilling = new AddressDto();

        existingBilling.setAddressType("Billing");
        existingBilling.setIdentifier("B1");

        AddressDto existingShipping = new AddressDto();

        existingShipping.setAddressType("Shipping");
        existingShipping.setIdentifier("S1");

        when(customerRepository.findByIdentifier("123")).thenReturn(existing);

        when(addressService.findAllByPhoneNo("123")).thenReturn(List.of(existingBilling, existingShipping));

        CustomerDto result = customerService.update(dto);

        assertTrue(result.isSuccess());

        assertEquals("Customer updated successfully", result.getMessage());

        verify(addressService).update(billing);

        verify(addressService).update(shipping);

    }

    @Test
    void updateWithoutAddressesTest() {

        Customer existing = new Customer();

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        when(customerRepository.findByIdentifier("123")).thenReturn(existing);

        when(addressService.findAllByPhoneNo("123")).thenReturn(List.of());

        customerService.update(dto);

        verify(addressService, never()).update(any());

    }

    @Test
    void updateNotFoundTest() {

        CustomerDto dto = new CustomerDto();

        dto.setIdentifier("123");

        when(customerRepository.findByIdentifier("123")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> customerService.update(dto));

    }

    @Test
    void deleteTest() {

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("123")).thenReturn(customer);

        boolean result = customerService.delete("123");

        assertTrue(result);

        verify(customerRepository).save(customer);

        verify(addressService).delete("123");

    }

    @Test
    void deleteNotFoundTest() {

        when(customerRepository.findByIdentifier("123")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> customerService.delete("123"));

    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();

        CustomerDto dto = new CustomerDto();

        Page<Customer> page = new PageImpl<>(List.of(customer));

        when(customerRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(0, result.getPage());

    }

    @Test
    void findAllSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Customer> spec = (root, query, cb) -> null;

        Customer customer = new Customer();

        CustomerDto dto = new CustomerDto();

        Page<Customer> page = new PageImpl<>(List.of(customer));

        when(customerRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<CustomerDto> result = customerService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

    }

    @Test
    void toggleStatusTest() {

        Customer customer = new Customer();

        customer.setStatus(true);

        Customer updated = new Customer();

        updated.setStatus(false);

        CustomerDto dto = new CustomerDto();

        when(customerRepository.findByIdentifier("123")).thenReturn(customer);

        when(customerRepository.save(customer)).thenReturn(updated);

        when(modelMapper.map(updated, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = customerService.toggleStatus("123");

        assertNotNull(result);

        verify(customerRepository).save(customer);

    }

    @Test
    void toggleStatusNotFoundTest() {

        when(customerRepository.findByIdentifier("123")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> customerService.toggleStatus("123"));

    }

}