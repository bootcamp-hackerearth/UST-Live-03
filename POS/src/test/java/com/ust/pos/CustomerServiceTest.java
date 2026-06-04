package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private AddressServiceImpl addressService;

    @Test
    void findByIdentifierTest() {
        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        Mockito.when(addressService.findByPhoneAndAddressType("1234567890", "billing")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneAndAddressType("1234567890", "shipping")).thenReturn(shipping);

        CustomerDto response = customerService.findByIdentifier("1234567890");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("1234567890", response.getIdentifier());
        Assertions.assertNotNull(response.getBillingAddress());
        Assertions.assertNotNull(response.getShippingAddress());
    }

    @Test
    void saveTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");
        customerDto.setPhoneNo("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(null);

        Customer customer = new Customer();
        Mockito.when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(addressService.save(Mockito.any(AddressDto.class))).thenReturn(billing);

        CustomerDto response = customerService.save(customerDto);
        Assertions.assertEquals("1234567890", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess() || !response.isSuccess());
        Mockito.verify(addressService, Mockito.times(2)).save(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void saveTestFailure() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");
        customerDto.setPhoneNo("1234567890");

        Customer existingCustomer = new Customer();
        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(existingCustomer);
        CustomerDto response = customerService.save(customerDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer existingCustomer = new Customer();
        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(existingCustomer);
        Mockito.doNothing().when(addressService).update(Mockito.any(AddressDto.class));
        Mockito.doNothing().when(modelMapper).map(customerDto, existingCustomer);
        Mockito.when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);
        Mockito.when(addressService.findByPhoneAndAddressType("1234567890", "billing")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneAndAddressType("1234567890", "shipping")).thenReturn(shipping);

        CustomerDto response = customerService.update(customerDto);
        Assertions.assertNotNull(response);
        Mockito.verify(addressService, Mockito.times(2)).update(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository).save(existingCustomer);
    }

    @Test
    void updateTestFailure() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");
        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(null);
        CustomerDto response = customerService.update(customerDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(customerRepository).deleteByIdentifier("1234567890");
        customerService.delete("1234567890");
        Mockito.verify(customerRepository).deleteByIdentifier("1234567890");
    }

    @Test
    void findAllTest() {
        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(customers, pageable, customers.size());

        Mockito.when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(customers),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(customerDtos);
        List<CustomerDto> response = customerService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("1234567890", response.get(0).getIdentifier());
    }

    @Test
    void toggleStatusSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("1234567890");
        customer.setStatus(false);

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");
        customerDto.setStatus(true);

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        CustomerDto response = customerService.toggleStatus("1234567890", true);
        Assertions.assertEquals("1234567890", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(null);
        CustomerDto response = customerService.toggleStatus("1234567890", true);
        Assertions.assertNull(response);
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }
}