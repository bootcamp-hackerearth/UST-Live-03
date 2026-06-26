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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    void saveTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(999999);
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        Customer customer = new Customer();
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        CustomerDto response = customerService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        Customer existing = new Customer();
        existing.setDeleted(false);
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(existing);
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        Customer existing = new Customer();
        existing.setDeleted(true);
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(existing);
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Customer customer = new Customer();
        customer.setPhoneNo(999999);
        CustomerDto dto = new CustomerDto();
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        Mockito.when(addressService.findByPhoneNoAndAddressType(Mockito.any(), Mockito.any()))
                .thenReturn(new AddressDto());
        CustomerDto response = customerService.findByIdentifier("C1");
        Assertions.assertNotNull(response);
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1")).thenReturn(null);
        CustomerDto response = customerService.findByIdentifier("C1");
        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(999999);
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);
        Customer existing = new Customer();
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(customerRepository.save(existing)).thenReturn(existing);
        Mockito.when(addressService.update(Mockito.any()))
                .thenReturn(new AddressDto());
        CustomerDto response = customerService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1")).thenReturn(null);
        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        CategoryRepositoryDummy();
    }

    private void CategoryRepositoryDummy() {
        Customer customer = new Customer();
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        customerService.delete("C1");
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void findAllTest() {
        Customer customer = new Customer();
        customer.setPhoneNo(999999);
        CustomerDto dto = new CustomerDto();
        List<Customer> list = List.of(customer);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Customer> page = new PageImpl<>(list);
        Mockito.when(customerRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        Mockito.when(addressService.findByPhoneNoAndAddressType(Mockito.any(), Mockito.any()))
                .thenReturn(new AddressDto());
        WsDto<CustomerDto> response = customerService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void buildAddressIdentifierTest() {
        AddressDto address = new AddressDto();
        address.setAddressLine("abc street");
        address.setZipcode(12345L);
        address.setAddressType("billing");
        String result = customerService.buildAddressIdentifier(address);
        Assertions.assertEquals("ABC STREET-12345-BILLING", result);
    }

    @Test
    void buildAddressIdentifierNullTest() {
        String result = customerService.buildAddressIdentifier(null);
        Assertions.assertNull(result);
    }

    @Test
    void saveTestNullBillingAddress() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(9999);
        dto.setBillingAddress(null);
        dto.setShippingAddress(new AddressDto());
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        Assertions.assertThrows(NullPointerException.class, () -> {
            customerService.save(dto);
        });
    }

    @Test
    void saveTestNullShippingAddress() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(9999);
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(null);
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        Assertions.assertThrows(NullPointerException.class, () -> {
            customerService.save(dto);
        });
    }

    @Test
    void updateOnlyBillingAddressTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(999999);
        AddressDto billing = new AddressDto();
        dto.setBillingAddress(billing);
        dto.setShippingAddress(null);
        Customer existing = new Customer();
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(customerRepository.save(existing)).thenReturn(existing);
        Mockito.when(addressService.update(Mockito.any()))
                .thenReturn(new AddressDto());
        CustomerDto response = customerService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateOnlyShippingAddressTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(999999);
        AddressDto shipping = new AddressDto();
        dto.setBillingAddress(null);
        dto.setShippingAddress(shipping);
        Customer existing = new Customer();
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(customerRepository.save(existing)).thenReturn(existing);
        Mockito.when(addressService.update(Mockito.any()))
                .thenReturn(new AddressDto());
        CustomerDto response = customerService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void deleteCustomerNullTest() {
        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);
        customerService.delete("C1");
        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());
        Mockito.verify(addressService, Mockito.never())
                .deleteByPhone(Mockito.any());
    }
}