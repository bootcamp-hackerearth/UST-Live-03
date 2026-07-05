package com.ust.pos;

import com.ust.pos.adress.service.AddressService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerDto customerDto;
    private Customer customer;
    private AddressDto billingAddressDto;
    private AddressDto shippingAddressDto;

    @BeforeEach
    void setUp() {
        billingAddressDto = new AddressDto();
        billingAddressDto.setAddressType("billing");

        shippingAddressDto = new AddressDto();
        shippingAddressDto.setAddressType("shipping");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");
        customerDto.setUsername("john_doe");
        customerDto.setBillingAddress(billingAddressDto);
        customerDto.setShippingAddress(shippingAddressDto);

        customer = new Customer();
        customer.setIdentifier("CUST-001");
        customer.setCustomerName("John Doe");
        customer.setStatus(true);
        customer.setDeleted(false);
    }

    @Test
    @DisplayName("Save Customer - Success with Addresses")
    void save_Success() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(addressService.save(any(AddressDto.class))).thenReturn(new AddressDto());
        when(cartService.save(any(CartDto.class))).thenReturn(new CartDto());

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Added Successfully"));
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
        verify(cartService).save(any(CartDto.class));
    }

    @Test
    @DisplayName("Save Customer - Success without Addresses")
    void save_Success_NoAddresses() {
        customerDto.setBillingAddress(null);
        customerDto.setShippingAddress(null);
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(cartService.save(any(CartDto.class))).thenReturn(new CartDto());

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertTrue(result.isSuccess());
        verify(customerRepository).save(customer);
        verify(addressService, never()).save(any(AddressDto.class));
        verify(cartService).save(any(CartDto.class));
    }

    @Test
    @DisplayName("Save Customer - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        customer.setDeleted(false);
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Save Customer - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        customer.setDeleted(true);
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Find All Customers - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findByDeletedFalse(pageable)).thenReturn(customerPage);
        when(modelMapper.map(eq(customerPage.getContent()), any(Type.class))).thenReturn(List.of(customerDto));

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Customers with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<Customer> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(spec, pageable)).thenReturn(customerPage);
        when(modelMapper.map(eq(customerPage.getContent()), any(Type.class))).thenReturn(List.of(customerDto));

        WsDto<CustomerDto> result = customerService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Active Customers - Success")
    void findAllActive_Success() {
        List<Customer> activeCustomers = List.of(customer);
        when(customerRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeCustomers);
        when(modelMapper.map(eq(activeCustomers), any(Type.class))).thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        billingAddressDto.setIdentifier("BILL-ID");
        shippingAddressDto.setIdentifier("SHIP-ID");
        List<AddressDto> addresses = List.of(billingAddressDto, shippingAddressDto);

        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNumber("CUST-001")).thenReturn(addresses);

        CustomerDto result = customerService.findByIdentifier("CUST-001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(billingAddressDto, result.getBillingAddress());
        Assertions.assertEquals(shippingAddressDto, result.getShippingAddress());
    }

    @Test
    @DisplayName("Find By Identifier - Failure: Not Found Exception")
    void findByIdentifier_Failure_NotFound() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.findByIdentifier("CUST-001"));
    }

    @Test
    @DisplayName("Update Customer - Success with Existing Addresses")
    void update_Success_WithExistingAddresses() {
        AddressDto existingBilling = new AddressDto();
        existingBilling.setAddressType("billing");
        existingBilling.setIdentifier("OLD-BILL-ID");

        AddressDto existingShipping = new AddressDto();
        existingShipping.setAddressType("shipping");
        existingShipping.setIdentifier("OLD-SHIP-ID");

        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);
        when(addressService.findAllByPhoneNumber("CUST-001")).thenReturn(List.of(existingBilling, existingShipping));

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Updated"));
        Assertions.assertEquals("OLD-BILL-ID", customerDto.getBillingAddress().getIdentifier());
        Assertions.assertEquals("OLD-SHIP-ID", customerDto.getShippingAddress().getIdentifier());
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).update(any(AddressDto.class));
    }

    @Test
    @DisplayName("Update Customer - Success without Existing Addresses")
    void update_Success_NoExistingAddresses() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);
        when(addressService.findAllByPhoneNumber("CUST-001")).thenReturn(List.of());

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertTrue(result.isSuccess());
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).update(any(AddressDto.class));
    }

    @Test
    @DisplayName("Update Customer - Failure: Not Found")
    void update_Failure_NotFound() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.toggleStatus("CUST-001");

        Assertions.assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Delete Customer - Success")
    void delete_Success() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(customer);
        when(addressService.delete("CUST-001")).thenReturn(true);

        boolean result = customerService.delete("CUST-001");

        Assertions.assertTrue(result);
        verify(customerRepository).save(customer);
        verify(addressService).delete("CUST-001");
    }

    @Test
    @DisplayName("Delete Customer - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(customerRepository.findByIdentifier("CUST-001")).thenReturn(null);

        boolean result = customerService.delete("CUST-001");

        Assertions.assertFalse(result);
        verify(customerRepository, never()).save(any(Customer.class));
        verify(addressService, never()).delete(anyString());
    }
}