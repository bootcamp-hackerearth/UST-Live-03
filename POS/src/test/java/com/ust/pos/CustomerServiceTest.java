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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifier_success() {
        Customer customer = new Customer();
        customer.setIdentifier("9999999999");
        CustomerDto dto = new CustomerDto();
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        Mockito.when(addressService.findByPhoneNoAndAddressType
                ("9999999999", "billing")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneNoAndAddressType
                ("9999999999", "shipping")).thenReturn(shipping);
        CustomerDto result = customerService.findByIdentifier("9999999999");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("9999999999", result.getPhoneNo());
        Assertions.assertEquals(billing, result.getBillingAddress());
        Assertions.assertEquals(shipping, result.getShippingAddress());
    }

    @Test
    void findByIdentifier_notFound() {
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        CustomerDto result = customerService.findByIdentifier("9999999999");
        Assertions.assertNull(result);
    }

    @Test
    void save_success() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9999999999");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(new Customer());
        CustomerDto result = customerService.save(dto);
        Assertions.assertNotNull(result);
        Mockito.verify(addressService, Mockito.times(2)).save(any(AddressDto.class));
        Mockito.verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void save_duplicateCustomer() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9999999999");
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(new Customer());
        CustomerDto result = customerService.save(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    void update_success() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());
        Customer existing = new Customer();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(existing);
        CustomerDto result = customerService.update(dto);
        Assertions.assertNotNull(result);
        Mockito.verify(addressService, Mockito.times(2)).update(any(AddressDto.class));
        Mockito.verify(customerRepository).save(existing);
    }

    @Test
    void update_notFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        CustomerDto result = customerService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    void delete_customer() {
        customerService.delete("9999999999");
        Mockito.verify(customerRepository).deleteByIdentifier("9999999999");
        Mockito.verify(addressService).delete("9999999999");
    }

    @Test
    void toggleStatus_success() {
        Customer customer = new Customer();
        customer.setStatus(false);
        CustomerDto dto = new CustomerDto();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        CustomerDto result = customerService.toggleStatus("9999999999", true);
        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(customer.isStatus());
        Assertions.assertEquals("Status updated successfully", result.getMessage()
        );
    }

    @Test
    void toggleStatus_notFound() {
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        CustomerDto result = customerService.toggleStatus("9999999999", true);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Customer not found", result.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    void findAll_withoutPageable() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();
        Mockito.when(customerRepository.findAll()).thenReturn(List.of(customer));
        Mockito.doReturn(List.of(dto)).when(modelMapper).map(Mockito.any(), Mockito.any(Type.class));
        List<CustomerDto> result = customerService.findAll(null);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(customerRepository).findAll();
        Mockito.verify(customerRepository, Mockito.never()).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void findAll_withPageable() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> page = new PageImpl<>(List.of(customer));
        Mockito.when(customerRepository.findAll(pageable)).thenReturn(page);
        Mockito.doReturn(List.of(dto)).when(modelMapper).map(Mockito.any(), Mockito.any(Type.class));
        List<CustomerDto> result = customerService.findAll(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Mockito.verify(customerRepository).findAll(pageable);
        Mockito.verify(customerRepository, Mockito.never()).findAll();
    }
}