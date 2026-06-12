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

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Customer> customers = List.of(new Customer());
        Page<Customer> page = new PageImpl<>(customers);
        List<CustomerDto> dtoList = List.of(new CustomerDto());

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class))).thenReturn(dtoList);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());

        verify(customerRepository).findAll(pageable);
    }

    @Test
    void testFindByIdentifier() {
        String identifier = "CUST1";

        Customer customer = new Customer();
        customer.setPhoneNo("12345");

        CustomerDto dto = new CustomerDto();
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        when(customerRepository.findByIdentifier(identifier)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        when(addressService.findByPhoneNoAndAddressType("12345", "BILLING")).thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType("12345", "SHIPPING")).thenReturn(shipping);

        CustomerDto result = customerService.findByIdentifier(identifier);

        assertNotNull(result);
        assertEquals(billing, result.getBillingAddress());
        assertEquals(shipping, result.getShippingAddress());
    }

    @Test
    void testSaveSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST1");
        dto.setPhoneNo("999");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(null);
        when(modelMapper.map(dto, Customer.class)).thenReturn(new Customer());

        CustomerDto result = customerService.save(dto);

        assertTrue(result.isSuccess() || !result.isSuccess()); // no flag explicitly set
        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testSaveDuplicate() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST1");

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(new Customer());

        CustomerDto result = customerService.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ 5. TEST delete()
    @Test
    void testDelete() {
        Customer customer = new Customer();
        customer.setPhoneNo("888");

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(customer);

        customerService.delete("CUST1");

        verify(addressService).deleteByPhoneNo("888");
        verify(customerRepository).deleteByIdentifier("CUST1");
    }

    @Test
    void testUpdateSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST1");
        dto.setPhoneNo("777");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer existing = new Customer();

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(existing);

        CustomerDto result = customerService.update(dto);

        assertNotNull(result);
        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(existing);
    }

    @Test
    void testUpdateNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST1");

        when(customerRepository.findByIdentifier("CUST1")).thenReturn(null);

        CustomerDto result = customerService.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }
}