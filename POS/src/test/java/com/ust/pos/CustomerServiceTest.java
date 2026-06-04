package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @Test
    void findByIdentifierSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = customerService.findByIdentifier("C1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifier("C1");

        Assertions.assertNull(result);
    }

    @Test
    void saveTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(123L);

        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        when(modelMapper.map(dto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(customer);

        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Customer customer = new Customer();
        customer.setIdentifier("C1");

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(123L);

        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();
        customer.setPhoneNo(123L);

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        when(addressService.findByPhoneNoAndAddressType(123L, "billingAddress")).thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType(123L, "billingAddress")).thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType(123L, "shippingAddress")).thenReturn(shipping);

        CustomerDto result = customerService.update(dto);

        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(modelMapper).map(dto, customer);
        verify(customerRepository).save(customer);

        Assertions.assertNotNull(result.getBillingAddress());
        Assertions.assertNotNull(result.getShippingAddress());
    }

    @Test
    void updateTestFailure() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto result = customerService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        customerService.delete("C1");
        verify(customerRepository).deleteByIdentifier("C1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Customer> page = mock(Page.class);

        List<Customer> customers = List.of(new Customer(), new Customer());
        List<CustomerDto> dtos = List.of(new CustomerDto(), new CustomerDto());

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(customers);
        when(modelMapper.map(eq(customers), any(Type.class))).thenReturn(dtos);

        List<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(customerRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(customers), any(Type.class));
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Customer customer = new Customer();
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        customerService.toggleStatus("C1");

        Assertions.assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Customer customer = new Customer();
        customer.setStatus(false);

        when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        customerService.toggleStatus("C1");

        Assertions.assertTrue(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusNullTest() {
        when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        customerService.toggleStatus("C1");

        verify(customerRepository, never()).save(any());
    }
}