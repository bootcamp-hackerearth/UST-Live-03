package com.ust.pos;

import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.address.service.AddressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    void findAll_WithPagination_ShouldReturnCustomerDtos() {

        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> customers = List.of(new Customer());
        Page<Customer> customerPage = new PageImpl<>(customers);
        List<CustomerDto> customerDtos = List.of(new CustomerDto());
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        Mockito.when(customerRepository.findAll(pageable))
                .thenReturn(customerPage);
        Mockito.when(modelMapper.map(customers, listType))
                .thenReturn(customerDtos);
        List<CustomerDto> response = customerService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(customerRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(customers, listType);
    }

    @Test
    void saveTest_Success() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBilling(billing);
        dto.setShipping(shipping);
        Mockito.when(customerRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        Mockito.doNothing()
                .when(addressService).save(shipping, billing);
        Customer customer = new Customer();
        Mockito.when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);
        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);
        CustomerDto response = customerService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(addressService).save(shipping, billing);
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void saveTest_Failure_WhenCustomerExists() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");
        Mockito.when(customerRepository.findByIdentifier("Admin"))
                .thenReturn(new Customer());
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(addressService, Mockito.never()).save(Mockito.any(), Mockito.any());
    }

    @Test
    void updateTest_Success() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBilling(billing);
        dto.setShipping(shipping);
        Mockito.when(customerRepository.findByIdentifier("Admin"))
                .thenReturn(new Customer());
        Customer mappedCustomer = new Customer();
        Mockito.when(modelMapper.map(dto, Customer.class))
                .thenReturn(mappedCustomer);
        Mockito.when(customerRepository.save(mappedCustomer))
                .thenReturn(mappedCustomer);
        Mockito.doNothing()
                .when(addressService).update(shipping, billing);
        CustomerDto response = customerService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(addressService).update(shipping, billing);
        Mockito.verify(customerRepository).save(mappedCustomer);
    }

    @Test
    void updateTest_Failure_WhenNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");
        Mockito.when(customerRepository.findByIdentifier("Admin"))
                .thenReturn(null);
        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(addressService, Mockito.never()).update(Mockito.any(), Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Customer customer = new Customer();
        customer.setIdentifier("Admin");
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");
        Mockito.when(customerRepository.findByIdentifier("Admin"))
                .thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        Mockito.when(addressService.findByIdentifierAndBilling("Admin"))
                .thenReturn(billing);
        Mockito.when(addressService.findByIdentifierAndShipping("Admin"))
                .thenReturn(shipping);
        CustomerDto response = customerService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getBilling());
        Assertions.assertNotNull(response.getShipping());
    }

    @Test
    void findAllTest() {
        Mockito.when(customerRepository.findAll())
                .thenReturn(List.of(new Customer()));
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(
                        Mockito.anyList(), Mockito.eq(listType)))
                .thenReturn(List.of(new CustomerDto()));
        Assertions.assertEquals(1, customerService.findAll().size());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(addressService).delete("Admin");
        Mockito.doNothing()
                .when(customerRepository).deleteByIdentifier("Admin");
        customerService.deleteByIdentifier("Admin");
        Mockito.verify(addressService).delete("Admin");
        Mockito.verify(customerRepository).deleteByIdentifier("Admin");
    }
}