package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    void findByIdentifier_Found() {

        Customer customer = new Customer();
        customer.setIdentifier("CUS001");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        CustomerDto result =
                customerService.findByIdentifier("CUS001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CUS001",
                result.getIdentifier());
    }

    @Test
    void findByIdentifier_NotFound() {

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        CustomerDto result =
                customerService.findByIdentifier("CUS001");

        Assertions.assertNull(result);
    }

    @Test
    void save_NewCustomer() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");
        dto.setPhoneNo(9876543210L);

        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("CUS001",
                result.getIdentifier());

        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(customer);

        Assertions.assertEquals(9876543210L,
                billing.getPhoneNo());

        Assertions.assertEquals(9876543210L,
                shipping.getPhoneNo());
    }

    @Test
    void save_CustomerAlreadyExists() {

        Customer existing = new Customer();

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(existing);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(addressService, never()).save(any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void update_CustomerExists() {

        Customer existing = new Customer();
        existing.setPhoneNo(9876543210L);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");
        dto.setPhoneNo(9876543210L);

        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        AddressDto updatedBilling = new AddressDto();
        updatedBilling.setAddressType("billingAddress");

        AddressDto updatedShipping = new AddressDto();
        updatedShipping.setAddressType("shippingAddress");

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(existing);

        when(addressService.findByPhoneNoAndAddressType(
                9876543210L,
                "billingAddress"))
                .thenReturn(updatedBilling);

        when(addressService.findByPhoneNoAndAddressType(
                9876543210L,
                "shippingAddress"))
                .thenReturn(updatedShipping);

        CustomerDto result = customerService.update(dto);

        Assertions.assertNotNull(result);

        verify(addressService).update(billing);
        verify(addressService).update(shipping);

        verify(modelMapper).map(dto, existing);

        verify(customerRepository).save(existing);

        Assertions.assertEquals(updatedBilling,
                result.getBillingAddress());

        Assertions.assertEquals(updatedShipping,
                result.getShippingAddress());
    }

    @Test
    void update_CustomerNotFound() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        CustomerDto result = customerService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(customerRepository, never()).save(any());

        verify(addressService, never()).update(any());
    }

    @Test
    void delete_CustomerExists() {

        Customer customer = new Customer();
        customer.setPhoneNo(9876543210L);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(customer);

        customerService.delete("CUS001");

        verify(addressService)
                .deleteByPhoneNo(9876543210L);

        verify(customerRepository)
                .deleteByIdentifier("CUS001");
    }

    @Test
    void delete_CustomerNotFound() {

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        customerService.delete("CUS001");

        verify(addressService, never())
                .deleteByPhoneNo(anyLong());

        verify(customerRepository)
                .deleteByIdentifier("CUS001");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Customer> customers =
                List.of(new Customer(), new Customer());

        Page<Customer> page =
                new PageImpl<>(customers);

        List<CustomerDto> dtoList =
                List.of(new CustomerDto(),
                        new CustomerDto());

        when(customerRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(customers),
                any(Type.class)))
                .thenReturn(dtoList);

        WsDto<CustomerDto> result =
                customerService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());
    }

    @Test
    void toggleStatus_CustomerFound() {

        Customer customer = new Customer();
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(customer);

        customerService.toggleStatus("CUS001");

        Assertions.assertFalse(customer.isStatus());

        verify(customerRepository)
                .save(customer);
    }

    @Test
    void toggleStatus_CustomerNotFound() {

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        customerService.toggleStatus("CUS001");

        verify(customerRepository, never())
                .save(any());
    }
}