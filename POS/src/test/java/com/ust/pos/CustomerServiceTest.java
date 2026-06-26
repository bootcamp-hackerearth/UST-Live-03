package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
    void findByIdentifierTest() {
        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
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
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(null);

        Customer customer = new Customer();
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        CustomerDto response = customerService.save(dto);
        Assertions.assertEquals("1234567890", response.getPhoneNo());
        Mockito.verify(addressService, Mockito.times(2)).save(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void saveFailure_existingActive() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        Customer existing = new Customer();
        existing.setDeleted(false);

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(existing);
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        Customer existing = new Customer();
        existing.setDeleted(true);

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(existing);
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void updateTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer existing = new Customer();
        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(existing);
        Mockito.doNothing().when(addressService).update(Mockito.any(AddressDto.class));
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(customerRepository.save(existing)).thenReturn(existing);

        Mockito.when(addressService.findByPhoneAndAddressType("1234567890", "billing")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneAndAddressType("1234567890", "shipping")).thenReturn(shipping);

        CustomerDto response = customerService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(addressService, Mockito.times(2)).update(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository).save(existing);
    }

    @Test
    void updateTestFailure() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        Mockito.when(customerRepository.findByIdentifier("123")).thenReturn(null);
        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        Mockito.when(customerRepository.findByIdentifier("1234567890")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        customerService.delete("1234567890");
        Mockito.verify(customerRepository).findByIdentifier("1234567890");
        Mockito.verify(customerRepository).save(customer);
        Mockito.verify(addressService).delete("1234567890");
        Assertions.assertTrue(customer.isDeleted());
    }

    @Test
    void findAllTest() {
        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("1234567890");

        List<Customer> list = List.of(customer);
        Page<Customer> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(customerRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<CustomerDto> response = customerService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("1234567890", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("Admin");
        customer.setStatus(false);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");
        dto.setStatus(true);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto response = customerService.toggleStatus("Admin", true);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(null);
        CustomerDto response = customerService.toggleStatus("Admin", true);
        Assertions.assertNull(response);
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }
}