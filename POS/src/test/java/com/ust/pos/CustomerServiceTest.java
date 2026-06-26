package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
    private AddressDto billing;
    private AddressDto shipping;

    @BeforeEach
    void setUp() {

        billing = new AddressDto();
        billing.setIdentifier("CUS001");

        shipping = new AddressDto();
        shipping.setIdentifier("CUS001");

        customer = new Customer();
        customer.setId(1L);
        customer.setIdentifier("CUS001");
        customer.setEmail("test@test.com");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUS001");
        customerDto.setEmail("test@test.com");
        customerDto.setBilling(billing);
        customerDto.setShipping(shipping);
    }

    @Test
    void save_ShouldReturnFailure_WhenCustomerAlreadyExists() {

        when(customerRepository.findByEmailAndIsDeleteFalse("test@test.com"))
                .thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Customer with email test@test.com already exists",
                result.getMessage());

        verify(customerRepository, never()).save(any());
        verify(addressService, never()).save(any(), any());
    }

    @Test
    void save_ShouldSaveCustomer_WhenCustomerDoesNotExist() {

        when(customerRepository.findByEmailAndIsDeleteFalse("test@test.com"))
                .thenReturn(null);

        when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertNotNull(result);

        verify(addressService).save(shipping, billing);
        verify(customerRepository).save(customer);
    }

    @Test
    void save_ShouldSetAddressIdentifiers() {

        billing.setIdentifier(null);
        shipping.setIdentifier(null);

        customerDto.setEmail("test@test.com");

        when(customerRepository.findByEmailAndIsDeleteFalse("test@test.com"))
                .thenReturn(null);

        when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(customer);

        customerService.save(customerDto);

        assertEquals("test@test.com", billing.getIdentifier());
        assertEquals("test@test.com", shipping.getIdentifier());

        verify(addressService).save(shipping, billing);
    }

    @Test
    void update_ShouldReturnFailure_WhenCustomerNotFound() {

        when(customerRepository.findByIdentifierAndIsDeleteFalse("CUS001"))
                .thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertEquals("Customer not found", result.getMessage());

        verify(customerRepository, never()).save(any());
        verify(addressService, never()).update(any(), any());
    }

    @Test
    void update_ShouldUpdateCustomer_WhenCustomerExists() {

        when(customerRepository.findByIdentifierAndIsDeleteFalse("CUS001"))
                .thenReturn(customer);

        CustomerDto result = customerService.update(customerDto);

        assertNotNull(result);

        verify(modelMapper).map(customerDto, customer);
        verify(customerRepository).save(customer);
        verify(addressService).update(shipping, billing);
    }

    @Test
    void update_ShouldSetAddressIdentifiers() {

        billing.setIdentifier(null);
        shipping.setIdentifier(null);

        when(customerRepository.findByIdentifierAndIsDeleteFalse("CUS001"))
                .thenReturn(customer);

        customerService.update(customerDto);

        assertEquals(customerDto.getEmail(), billing.getIdentifier());
        assertEquals(customerDto.getEmail(), shipping.getIdentifier());

        verify(addressService).update(shipping, billing);
    }

    @Test
    void findByIdentifier_ShouldReturnCustomerWithAddresses() {

        // Arrange
        Customer customer = new Customer();
        customer.setIdentifier("CUS001");
        customer.setEmail("test@test.com");

        CustomerDto customerDto = new CustomerDto();

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        when(customerRepository.findByIdentifierAndIsDeleteFalse("CUS001"))
                .thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(customerDto);

        when(addressService.findByIdentifierAndBilling(customer.getEmail()))
                .thenReturn(billing);

        when(addressService.findByIdentifierAndShipping(customer.getEmail()))
                .thenReturn(shipping);

        // Act
        CustomerDto result = customerService.findByIdentifier("CUS001");

        // Assert
        assertNotNull(result);
        assertEquals(billing, result.getBilling());
        assertEquals(shipping, result.getShipping());

        verify(customerRepository).findByIdentifierAndIsDeleteFalse("CUS001");
        verify(modelMapper).map(customer, CustomerDto.class);
        verify(addressService).findByIdentifierAndBilling("test@test.com");
        verify(addressService).findByIdentifierAndShipping("test@test.com");
    }

    @Test
    void findAll_ShouldReturnMappedList() {

        List<Customer> customers = List.of(customer);
        List<CustomerDto> expected = List.of(customerDto);

        when(customerRepository.findByIsDeleteFalse())
                .thenReturn(customers);

        when(modelMapper.map(
                eq(customers),
                any(Type.class)))
                .thenReturn(expected);

        List<CustomerDto> result =
                customerService.findAll();

        assertEquals(1, result.size());

        verify(customerRepository)
                .findByIsDeleteFalse();
    }

    @Test
    void delete_ShouldSoftDelete_WhenCustomerExists() {

        when(customerRepository.findByEmailAndIsDeleteFalse("CUS001"))
                .thenReturn(customer);

        customerService.delete("CUS001");

        assertTrue(customer.isDelete());

        verify(addressService).delete("CUS001");
        verify(customerRepository).save(customer);
    }

    @Test
    void delete_ShouldDoNothing_WhenCustomerNotFound() {

        when(customerRepository.findByEmailAndIsDeleteFalse("CUS001"))
                .thenReturn(null);

        customerService.delete("CUS001");

        verify(customerRepository, never()).save(any());
        verify(addressService, never()).delete(any());
    }

    @Test
    void findAll_WithPageable_ShouldReturnMappedList() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> page =
                new PageImpl<>(List.of(customer));

        List<CustomerDto> expected =
                List.of(customerDto);

        when(customerRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()),
                any(Type.class)))
                .thenReturn(expected);

        List<CustomerDto> result =
                customerService.findAll(pageable);

        assertEquals(1, result.size());

        verify(customerRepository)
                .findByIsDeleteFalse(pageable);
    }

    @Test
    void findAll_WithSearch_ShouldReturnMappedPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> page =
                new PageImpl<>(List.of(customer));

        when(customerRepository
                .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(
                        "CUS",
                        pageable))
                .thenReturn(page);

        when(modelMapper.map(
                any(Customer.class),
                eq(CustomerDto.class)))
                .thenReturn(customerDto);

        Page<CustomerDto> result =
                customerService.findAll(pageable, "CUS");

        assertEquals(1, result.getTotalElements());

        verify(customerRepository)
                .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse(
                        "CUS",
                        pageable);
    }

    @Test
    void findAll_WithoutSearch_ShouldReturnMappedPage() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> page =
                new PageImpl<>(List.of(customer));

        when(customerRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                any(Customer.class),
                eq(CustomerDto.class)))
                .thenReturn(customerDto);

        Page<CustomerDto> result =
                customerService.findAll(pageable, "");

        assertEquals(1, result.getTotalElements());

        verify(customerRepository)
                .findByIsDeleteFalse(pageable);
    }
}