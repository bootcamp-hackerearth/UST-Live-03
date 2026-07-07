package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setId(1L);
        customer.setIdentifier("CUS001");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUS001");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBilling(billing);
        customerDto.setShipping(shipping);
    }

    @Test
    void save_WhenCustomerAlreadyExists_ShouldReturnFailure() {

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(customer);

        CustomerDto response =
                customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Customer already exists",
                response.getMessage()
        );

        Mockito.verify(customerRepository)
                .findByIdentifierAndDeletedFalse("CUS001");

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any(Customer.class));
    }

    @Test
    void save_ShouldSaveCustomerSuccessfully() {

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(customerDto, Customer.class)
        ).thenReturn(customer);

        CustomerDto response =
                customerService.save(customerDto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(addressService)
                .save(
                        customerDto.getShipping(),
                        customerDto.getBilling()
                );

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void save_WithNullAddresses_ShouldCreateAddresses() {

        customerDto.setBilling(null);
        customerDto.setShipping(null);

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(customerDto, Customer.class)
        ).thenReturn(customer);

        CustomerDto response =
                customerService.save(customerDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertNotNull(customerDto.getBilling());
        Assertions.assertNotNull(customerDto.getShipping());

        Mockito.verify(addressService)
                .save(
                        Mockito.any(AddressDto.class),
                        Mockito.any(AddressDto.class)
                );

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void update_WhenCustomerNotFound_ShouldReturnFailure() {

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(null);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Customer not found",
                response.getMessage()
        );

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any(Customer.class));

        Mockito.verify(addressService, Mockito.never())
                .update(
                        Mockito.any(AddressDto.class),
                        Mockito.any(AddressDto.class)
                );
    }

    @Test
    void update_ShouldUpdateCustomerSuccessfully() {

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(customerDto, Customer.class)
        ).thenReturn(customer);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Customer updated successfully",
                response.getMessage()
        );

        Mockito.verify(customerRepository)
                .save(customer);

        Mockito.verify(addressService)
                .update(
                        customerDto.getShipping(),
                        customerDto.getBilling()
                );
    }

    @Test
    void update_WithoutAddresses_ShouldUpdateCustomer() {

        customerDto.setBilling(null);
        customerDto.setShipping(null);

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(customerDto, Customer.class)
        ).thenReturn(customer);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(customerRepository)
                .save(customer);

        Mockito.verify(addressService, Mockito.never())
                .update(
                        Mockito.any(AddressDto.class),
                        Mockito.any(AddressDto.class)
                );
    }

    @Test
    void findByIdentifier_ShouldReturnCustomerDto() {

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(
                customerRepository.findByIdentifierAndDeletedFalse("CUS001")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(customer, CustomerDto.class)
        ).thenReturn(customerDto);

        Mockito.when(
                addressService.findByIdentifierAndBilling("CUS001")
        ).thenReturn(billing);

        Mockito.when(
                addressService.findByIdentifierAndShipping("CUS001")
        ).thenReturn(shipping);

        CustomerDto response =
                customerService.findByIdentifier("CUS001");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                billing,
                response.getBilling()
        );
        Assertions.assertEquals(
                shipping,
                response.getShipping()
        );

        Mockito.verify(customerRepository)
                .findByIdentifierAndDeletedFalse("CUS001");

        Mockito.verify(addressService)
                .findByIdentifierAndBilling("CUS001");

        Mockito.verify(addressService)
                .findByIdentifierAndShipping("CUS001");
    }
}