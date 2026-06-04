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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void saveCustomerSuccess() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBilling(billing);
        customerDto.setShipping(shipping);

        Mockito.when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(new Customer());

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertEquals("CUST1", billing.getIdentifier());
        Assertions.assertEquals("CUST1", shipping.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess()); // default false unless set

        Mockito.verify(addressService)
                .save(shipping, billing);
        Mockito.verify(customerRepository)
                .save(Mockito.any(Customer.class));
    }
    @Test
    void findAll_WithPagination_ShouldReturnCustomerDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        List<Customer> customers = List.of(new Customer());
        Page<Customer> customerPage = new PageImpl<>(customers);

        List<CustomerDto> customerDtos = List.of(new CustomerDto());

        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();

        Mockito.when(customerRepository.findAll(pageable))
                .thenReturn(customerPage);

        Mockito.when(modelMapper.map(customers, listType))
                .thenReturn(customerDtos);

        // Act
        List<CustomerDto> response = customerService.findAll(pageable);

        // Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());

        Mockito.verify(customerRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(customers, listType);
    }

    @Test
    void saveCustomerAlreadyExists() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(new Customer());

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer already exists", response.getMessage());

        Mockito.verify(addressService, Mockito.never())
                .save(Mockito.any(), Mockito.any());
        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateCustomerSuccess() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBilling(billing);
        customerDto.setShipping(shipping);

        Mockito.when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(new Customer());
        Mockito.when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(new Customer());

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertEquals("CUST1", billing.getIdentifier());
        Assertions.assertEquals("CUST1", shipping.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(customerRepository)
                .save(Mockito.any(Customer.class));
        Mockito.verify(addressService)
                .update(shipping, billing);
    }

    @Test
    void updateCustomerNotFound() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(null);

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer not found", response.getMessage());

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());
        Mockito.verify(addressService, Mockito.never())
                .update(Mockito.any(), Mockito.any());
    }

    @Test
    void findCustomerByIdentifierTest() {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(customerDto);
        Mockito.when(addressService.findByIdentifierAndBilling("CUST1"))
                .thenReturn(billing);
        Mockito.when(addressService.findByIdentifierAndShipping("CUST1"))
                .thenReturn(shipping);

        CustomerDto response = customerService.findByIdentifier("CUST1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(billing, response.getBilling());
        Assertions.assertEquals(shipping, response.getShipping());
    }

    @Test
    void findAllCustomersTest() {
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer());
        customers.add(new Customer());

        List<CustomerDto> dtoList = new ArrayList<>();
        dtoList.add(new CustomerDto());
        dtoList.add(new CustomerDto());

        Mockito.when(customerRepository.findAll())
                .thenReturn(customers);
        Mockito.when(modelMapper.map(
                Mockito.eq(customers),
                Mockito.any(Type.class))
        ).thenReturn(dtoList);

        List<CustomerDto> response = customerService.findAll();

        Assertions.assertEquals(2, response.size());

        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void deleteCustomerTest() {
        String identifier = "CUST1";

        Mockito.doNothing()
                .when(addressService)
                .delete(identifier);
        Mockito.doNothing()
                .when(customerRepository)
                .deleteByIdentifier(identifier);

        customerService.deleteByIdentifier(identifier);

        Mockito.verify(addressService)
                .delete(identifier);
        Mockito.verify(customerRepository)
                .deleteByIdentifier(identifier);
    }
}