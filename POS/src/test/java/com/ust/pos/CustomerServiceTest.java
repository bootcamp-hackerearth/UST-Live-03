package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setPhoneNo(9876543210L);
        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST001");
        customerDto.setPhoneNo(9876543210L);
        AddressDto billing = new AddressDto();
        billing.setAddressLine("Billing Street");
        billing.setZipcode(600001L);
        AddressDto shipping = new AddressDto();
        shipping.setAddressLine("Shipping Street");
        shipping.setZipcode(600002L);
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);
    }

    @Test
    void findByIdentifier_WhenCustomerExists() {
        when(customerRepository.findByIdentifier("CUST001"))
                .thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(customerDto);
        CustomerDto result =
                customerService.findByIdentifier("CUST001");
        assertNotNull(result);
        assertEquals("CUST001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_WhenCustomerNotExists() {
        when(customerRepository.findByIdentifier("CUST001"))
                .thenReturn(null);
        CustomerDto result =
                customerService.findByIdentifier("CUST001");
        assertNull(result);
    }

    @Test
    void save_WhenCustomerAlreadyExists() {
        when(customerRepository.findByIdentifier("CUST001"))
                .thenReturn(customer);
        CustomerDto result =
                customerService.save(customerDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void save_WhenCustomerDoesNotExist() {
        when(customerRepository.findByIdentifier("CUST001"))
                .thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(customer);
        CustomerDto result =
                customerService.save(customerDto);
        verify(addressService, times(2))
                .save(any(AddressDto.class));
        verify(addressService, times(2)).save(any(AddressDto.class));
        verify(customerRepository).save(any(Customer.class));

        assertNotNull(result);
    }

    @Test
    void update_WhenCustomerNotFound() {
        when(customerRepository.findByIdentifier("CUST001"))
                .thenReturn(null);
        CustomerDto result =
                customerService.update(customerDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void update_WhenCustomerFound() {
        when(customerRepository.findByIdentifier("CUST001"))
                .thenReturn(customer);
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        when(addressService.findByPhoneNoAndAddressType(
                anyLong(), eq("billingAddress")))
                .thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType(
                anyLong(), eq("shippingAddress")))
                .thenReturn(shipping);
        CustomerDto result =
                customerService.update(customerDto);
        verify(addressService, times(2))
                .update(any(AddressDto.class));
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void delete_ShouldCallRepository() {
        customerService.delete("CUST001");
        verify(customerRepository)
                .deleteByIdentifier("CUST001");
    }

    @Test
    void findAll_ShouldReturnList() {
        Pageable pageable =
                PageRequest.of(0, 10);
        Page<Customer> page =
                new PageImpl<>(List.of(customer));
        List<CustomerDto> dtoList =
                List.of(customerDto);
        when(customerRepository.findAll(pageable))
                .thenReturn(page);
        when(modelMapper.map(
                any(),
                any(java.lang.reflect.Type.class)))
                .thenReturn(dtoList);
        List<CustomerDto> result =
                customerService.findAll(pageable);
        assertEquals(1, result.size());
    }

    @Test
    void buildAddressIdentifier_WhenAddressNull() {
        String result =
                customerService.buildAddressIdentifier(null);
        assertNull(result);
    }

    @Test
    void buildAddressIdentifier_WhenAddressPresent() {
        AddressDto address = new AddressDto();
        address.setAddressLine("Anna Nagar");
        address.setZipcode(600040L);
        address.setAddressType("billing");
        String result =
                customerService.buildAddressIdentifier(address);
        assertEquals(
                "ANNA NAGAR-600040-BILLING",
                result
        );
    }
}