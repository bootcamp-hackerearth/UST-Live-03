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
    void saveTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBilling(billing);
        dto.setShipping(shipping);

        Mockito.when(customerRepository.findByIdentifier("C1"))
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
    void saveTestFailureWhenCustomerExists() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1"))
                .thenReturn(new Customer());

        CustomerDto response = customerService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(addressService, Mockito.never()).save(Mockito.any(), Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        dto.setBilling(billing);
        dto.setShipping(shipping);

        Mockito.when(customerRepository.findByIdentifier("C1"))
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
    void updateTestFailureWhenNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1"))
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
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1"))
                .thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(addressService.findByIdentifierAndBilling("C1"))
                .thenReturn(billing);
        Mockito.when(addressService.findByIdentifierAndShipping("C1"))
                .thenReturn(shipping);

        CustomerDto response = customerService.findByIdentifier("C1");

        Assertions.assertEquals("C1", response.getIdentifier());
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
                .when(addressService).delete("C1");
        Mockito.doNothing()
                .when(customerRepository).deleteByIdentifier("C1");

        customerService.deleteByIdentifier("C1");

        Mockito.verify(addressService).delete("C1");
        Mockito.verify(customerRepository).deleteByIdentifier("C1");
    }

    @Test
    void findAllWithPaginationShouldReturnCustomerDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Customer> customers = List.of(new Customer());
        Page<Customer> customerPage = new PageImpl<>(customers);

        List<CustomerDto> customerDtos = List.of(new CustomerDto());

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

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
}