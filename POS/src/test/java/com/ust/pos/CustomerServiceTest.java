package com.ust.pos;

import com.ust.pos.adress.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
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

import java.lang.reflect.Type;
import java.util.Collections;
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

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerDto customerDto;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST001");
        customerDto.setUsername("user1");

        customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setCustomerName("John Doe");
        customer.setStatus(true);
    }

    @Test
    @DisplayName("Save - New Customer with Both Addresses")
    void save_Success_Full() {
        AddressDto billingDto = new AddressDto();
        AddressDto shippingDto = new AddressDto();
        customerDto.setBillingAddress(billingDto);
        customerDto.setShippingAddress(shippingDto);

        when(customerRepository.findByIdentifier("CUST001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST001 Added Successfully", result.getMessage());
        Assertions.assertEquals("user1_billing_CUST001", billingDto.getIdentifier());
        Assertions.assertEquals("CUST001", billingDto.getPhoneNo());
        Assertions.assertEquals("John Doe", billingDto.getCustomerName());
        Assertions.assertEquals("billing", billingDto.getAddressType());

        Assertions.assertEquals("user1_shipping_CUST001", shippingDto.getIdentifier());
        Assertions.assertEquals("CUST001", shippingDto.getPhoneNo());
        Assertions.assertEquals("John Doe", shippingDto.getCustomerName());
        Assertions.assertEquals("shipping", shippingDto.getAddressType());

        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
    }

    @Test
    @DisplayName("Save - New Customer with No Addresses")
    void save_Success_Minimal() {
        customerDto.setBillingAddress(null);
        customerDto.setShippingAddress(null);

        when(customerRepository.findByIdentifier("CUST001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST001 Added Successfully", result.getMessage());
        verify(customerRepository).save(customer);
        verify(addressService, never()).save(any());
    }

    @Test
    @DisplayName("Save - Already Exists")
    void save_Failure_Exists() {
        when(customerRepository.findByIdentifier("CUST001")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST001 already exists", result.getMessage());
        verify(customerRepository, never()).save(any());
        verify(addressService, never()).save(any());
    }

    @Test
    @DisplayName("Update - Full Address Sync (Existing Billing and Shipping)")
    void update_Success_FullSync() {
        AddressDto existingBill = new AddressDto();
        existingBill.setAddressType("billing");
        existingBill.setIdentifier("OLD_BILL_ID");

        AddressDto existingShip = new AddressDto();
        existingShip.setAddressType("shipping");
        existingShip.setIdentifier("OLD_SHIP_ID");

        AddressDto billingDto = new AddressDto();
        AddressDto shippingDto = new AddressDto();
        customerDto.setBillingAddress(billingDto);
        customerDto.setShippingAddress(shippingDto);

        when(customerRepository.findByIdentifier("CUST001")).thenReturn(customer);
        when(addressService.findAllByPhoneNumber("CUST001")).thenReturn(List.of(existingBill, existingShip));

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST001 Updated", result.getMessage());
        Assertions.assertEquals("OLD_BILL_ID", billingDto.getIdentifier());
        Assertions.assertEquals("OLD_SHIP_ID", shippingDto.getIdentifier());

        verify(modelMapper).map(customerDto, customer);
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).update(any(AddressDto.class));
    }

    @Test
    @DisplayName("Update - Addresses Provided But None Exist in DB")
    void update_Success_NoExistingAddresses() {
        AddressDto billingDto = new AddressDto();
        AddressDto shippingDto = new AddressDto();
        customerDto.setBillingAddress(billingDto);
        customerDto.setShippingAddress(shippingDto);

        when(customerRepository.findByIdentifier("CUST001")).thenReturn(customer);
        when(addressService.findAllByPhoneNumber("CUST001")).thenReturn(Collections.emptyList());

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNull(billingDto.getIdentifier());
        Assertions.assertNull(shippingDto.getIdentifier());
        verify(addressService, times(2)).update(any(AddressDto.class));
    }

    @Test
    @DisplayName("Update - Customer Not Found")
    void update_Failure_NotFound() {
        when(customerRepository.findByIdentifier("CUST001")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST001 not found", result.getMessage());
        verify(customerRepository, never()).save(any());
        verify(addressService, never()).update(any());
    }

    @Test
    @DisplayName("Find All - Pagination Support")
    void findAll_Paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> list = List.of(customer);
        Page<Customer> page = new PageImpl<>(list);

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find All Active")
    void findAllActive_Success() {
        List<Customer> list = List.of(customer);
        when(customerRepository.findAllByStatus(true)).thenReturn(list);
        when(modelMapper.map(eq(list), any(Type.class))).thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier")
    void findByIdentifier_Success() {
        when(customerRepository.findByIdentifier("CUST001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findByIdentifier("CUST001");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status")
    void toggleStatus_Logic() {
        customer.setStatus(true);
        when(customerRepository.findByIdentifier("CUST001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        customerService.toggleStatus("CUST001");

        Assertions.assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Delete - Cascade Verification")
    void delete_Success() {
        boolean deleted = customerService.delete("CUST001");

        Assertions.assertTrue(deleted);
        verify(customerRepository).deleteByIdentifier("CUST001");
        verify(addressService).delete("CUST001");
    }
}