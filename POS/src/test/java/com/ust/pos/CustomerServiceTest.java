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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
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
    private AddressService addressService;

    @Test
    void findByIdentifier_Success() {

        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto response = customerService.findByIdentifier("C1");
        Assertions.assertNotNull(response);

    }
    @Test
    void findByIdentifier_NotFound() {

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto response = customerService.findByIdentifier("C1");
        Assertions.assertNull(response);

    }

    @Test
    void saveTest_Success() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNumber(123L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(customer);

        customerService.save(dto);
        Assertions.assertEquals(123L, billing.getPhoneNumber());
        Assertions.assertEquals(123L, shipping.getPhoneNumber());

        Mockito.verify(addressService).save(billing);
        Mockito.verify(addressService).save(shipping);

    }

    @Test
    void saveTest_Duplicate() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(new Customer());

        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());

    }

    @Test
    void updateTest_Success() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNumber(123L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer existing = new Customer();
        existing.setPhoneNumber(123L);

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(existing);
        Mockito.when(addressService.findByPhoneNoAndAddressType(123L, "BILLING")).thenReturn(new AddressDto());
        Mockito.when(addressService.findByPhoneNoAndAddressType(123L, "SHIPPING")).thenReturn(new AddressDto());

        CustomerDto response = customerService.update(dto);
        Assertions.assertNotNull(response);

    }

    @Test
    void updateTest_NotFound() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));

    }

    @Test
    void deleteTest_Success() {

        Customer customer = new Customer();
        customer.setPhoneNumber(123L);

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        customerService.delete("C1");

        Mockito.verify(addressService).deleteByPhoneNumber(123L);
        Mockito.verify(customerRepository).deleteByIdentifier("C1");

    }

    @Test
    void deleteTest_NotFound() {

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        customerService.delete("C1");
        Mockito.verify(customerRepository, Mockito.never()).deleteByIdentifier(Mockito.any());

    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        List<Customer> customerList = List.of(customer);

        Page<Customer> customerPage = new PageImpl<>(customerList);

        Mockito.when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        List<CustomerDto> customerDtoList = List.of(customerDto);
        Mockito.when(modelMapper.map(Mockito.eq(customerPage.getContent()), Mockito.any(Type.class))).thenReturn(customerDtoList);

        List<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("CUST1", result.get(0).getIdentifier());
        Mockito.verify(customerRepository).findAll(pageable);

    }

}
