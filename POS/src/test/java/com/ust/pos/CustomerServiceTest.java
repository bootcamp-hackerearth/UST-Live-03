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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

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

        Customer customer = new Customer();

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(customerDto, Customer.class)
        ).thenReturn(customer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertNull(response.getMessage());

        Assertions.assertEquals(
                "CUST1",
                billing.getIdentifier()
        );

        Assertions.assertEquals(
                "CUST1",
                shipping.getIdentifier()
        );

        Mockito.verify(addressService)
                .save(shipping, billing);

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void saveCustomerAlreadyExists() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(new Customer());

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Customer already exists",
                response.getMessage()
        );

        Mockito.verify(customerRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateCustomerSuccess() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBilling(billing);
        customerDto.setShipping(shipping);

        Customer existingCustomer = new Customer();

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(existingCustomer);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertNull(response.getMessage());

        Assertions.assertEquals(
                "CUST1",
                billing.getIdentifier()
        );

        Assertions.assertEquals(
                "CUST1",
                shipping.getIdentifier()
        );

        Mockito.verify(modelMapper)
                .map(customerDto, existingCustomer);

        Mockito.verify(customerRepository)
                .save(existingCustomer);

        Mockito.verify(addressService)
                .update(shipping, billing);
    }

    @Test
    void updateCustomerNotFound() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(null);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Customer not found",
                response.getMessage()
        );

        Mockito.verify(customerRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void findCustomerByIdentifierTest() {

        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(customer, CustomerDto.class)
        ).thenReturn(customerDto);

        Mockito.when(
                addressService.findByIdentifierAndBilling("CUST1")
        ).thenReturn(billing);

        Mockito.when(
                addressService.findByIdentifierAndShipping("CUST1")
        ).thenReturn(shipping);

        CustomerDto response =
                customerService.findByIdentifier("CUST1");

        Assertions.assertEquals(
                billing,
                response.getBilling()
        );

        Assertions.assertEquals(
                shipping,
                response.getShipping()
        );
    }

    @Test
    void findAllCustomersTest() {

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer());
        customers.add(new Customer());

        List<CustomerDto> dtoList = new ArrayList<>();
        dtoList.add(new CustomerDto());
        dtoList.add(new CustomerDto());

        Mockito.when(
                customerRepository.findByDeletedFalse()
        ).thenReturn(customers);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(customers),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<CustomerDto> response =
                customerService.findAll();

        Assertions.assertEquals(
                2,
                response.size()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Customer> customers =
                List.of(new Customer());

        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, 1);

        List<CustomerDto> customerDtos =
                List.of(new CustomerDto());

        Type listType =
                new TypeToken<List<CustomerDto>>(){}.getType();

        Mockito.when(
                customerRepository.findByDeletedFalse(pageable)
        ).thenReturn(customerPage);

        Mockito.when(
                modelMapper.map(customers, listType)
        ).thenReturn(customerDtos);

        WsDto<CustomerDto> response =
                customerService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void deleteCustomerTest() {

        Customer customer = new Customer();
        customer.setDeleted(false);

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(customer);

        customerService.deleteByIdentifier("CUST1");

        Assertions.assertTrue(customer.getDeleted());

        Mockito.verify(customerRepository)
                .save(customer);

        Mockito.verify(addressService)
                .delete("CUST1");
    }

    @Test
    void deleteCustomerNotFoundTest() {

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUST1")
        ).thenReturn(null);

        customerService.deleteByIdentifier("CUST1");

        Mockito.verify(customerRepository,
                Mockito.never()).save(Mockito.any());

        Mockito.verify(addressService,
                Mockito.never()).delete(Mockito.anyString());
    }

    @Test
    void findByEmailTest() {

        Customer customer = new Customer();
        customer.setEmail("test@test.com");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setEmail("test@test.com");

        Mockito.when(
                customerRepository.findByEmailAndDeletedFalse("test@test.com")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(customer, CustomerDto.class)
        ).thenReturn(customerDto);

        CustomerDto response =
                customerService.findByEmail("test@test.com");

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "test@test.com",
                response.getEmail()
        );
    }

    @Test
    void findByEmailNotFoundTest() {

        Mockito.when(
                customerRepository.findByEmailAndDeletedFalse("test@test.com")
        ).thenReturn(null);

        CustomerDto response =
                customerService.findByEmail("test@test.com");

        Assertions.assertNull(response);
    }

    @Test
    void toggleStatusSuccess() {

        Customer customer = new Customer();
        customer.setStatus(true);

        Mockito.when(
                customerRepository.findByEmailAndDeletedFalse("test@test.com")
        ).thenReturn(customer);

        customerService.toggleStatus("test@test.com");

        Assertions.assertFalse(customer.getStatus());

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void toggleStatusNotFound() {

        Mockito.when(
                customerRepository.findByEmailAndDeletedFalse("test@test.com")
        ).thenReturn(null);

        customerService.toggleStatus("test@test.com");

        Mockito.verify(customerRepository,
                Mockito.never()).save(Mockito.any());
    }
}