package com.ust.pos;

import com.ust.pos.address.service.AddressService;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    void save_success() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");
        Customer mapped = new Customer();
        mapped.setPhoneNo("9876543210");
        Customer saved = new Customer();
        saved.setIdentifier("9876543210");
        saved.setPhoneNo("9876543210");
        saved.setStatus(true);
        Mockito.when(customerRepository.findByPhoneNo("9876543210")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(mapped);
        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(saved);
        CustomerDto response = customerService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Customer created successfully", response.getMessage());
    }

    @Test
    void save_failure_emptyPhone() {
        CustomerDto dto = new CustomerDto();
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Phone number is required", response.getMessage());
    }

    @Test
    void save_failure_invalidPhone() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("123");
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Phone number must be 10 digits", response.getMessage());
    }

    @Test
    void save_failure_duplicate() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");
        Mockito.when(customerRepository.findByPhoneNo("9876543210")).thenReturn(new Customer());
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer already exists", response.getMessage());
    }

    @Test
    void find_success() {
        Customer customer = new Customer();
        customer.setIdentifier("9876543210");
        customer.setPhoneNo("9876543210");
        CustomerDto mapped = new CustomerDto();
        mapped.setIdentifier("9876543210");
        Mockito.when(customerRepository.findByIdentifier("9876543210")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(mapped);
        Mockito.when(addressService.findByPhoneNoAndAddressType("9876543210", "billing"))
                .thenReturn(new AddressDto());
        Mockito.when(addressService.findByPhoneNoAndAddressType("9876543210", "shipping"))
                .thenReturn(new AddressDto());
        CustomerDto response = customerService.findByIdentifier("9876543210");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("9876543210", response.getIdentifier());
    }

    @Test
    void find_notFound() {
        Mockito.when(customerRepository.findByIdentifier("123")).thenReturn(null);
        CustomerDto response = customerService.findByIdentifier("123");
        Assertions.assertNull(response);
    }

    @Test
    void update_success() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9876543210");
        dto.setPhoneNo("9876543210");
        Customer existing = new Customer();
        existing.setIdentifier("9876543210");
        existing.setPhoneNo("9876543210");
        Mockito.when(customerRepository.findByIdentifier("9876543210"))
                .thenReturn(existing);
        Mockito.when(customerRepository.save(existing)).thenReturn(existing);
        CustomerDto response = customerService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Customer updated successfully", response.getMessage());
    }

    @Test
    void update_failure_notFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");
        Mockito.when(customerRepository.findByIdentifier("123")).thenReturn(null);
        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer not found", response.getMessage());
    }

    @Test
    void update_failure_phoneChanged() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9876543210");
        dto.setPhoneNo("1111111111");
        Customer existing = new Customer();
        existing.setIdentifier("9876543210");
        existing.setPhoneNo("9876543210");
        Mockito.when(customerRepository.findByIdentifier("9876543210"))
                .thenReturn(existing);
        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Phone number is read-only and cannot be updated", response.getMessage());
    }

    @Test
    void delete_test() {
        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setPhoneNo("9876543210");
        Mockito.when(customerRepository.findByIdentifier("123")).thenReturn(customer);
        Mockito.doNothing().when(addressService).delete("9876543210");
        customerService.delete("123");
        Mockito.verify(addressService).delete("9876543210");
        Mockito.verify(customerRepository).delete(customer);
    }

    @Test
    void active_test() {
        Customer active = new Customer();
        active.setStatus(true);
        Customer inactive = new Customer();
        inactive.setStatus(false);
        Mockito.when(customerRepository.findAll()).thenReturn(List.of(active, inactive));
        List<CustomerDto> result = customerService.findActive();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void toggle_success() {
        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setStatus(true);
        Mockito.when(customerRepository.findByIdentifier("123")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        CustomerDto response = customerService.toggleStatus("123");
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Customer status updated successfully", response.getMessage());
    }

    @Test
    void toggle_notFound() {
        Mockito.when(customerRepository.findByIdentifier("123")).thenReturn(null);
        CustomerDto response = customerService.toggleStatus("123");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer not found", response.getMessage());
    }
}