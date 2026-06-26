package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        AddressDto billing = new AddressDto();
        billing.setAddressType("billingAddress");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shippingAddress");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST123");
        customerDto.setPhoneNo(9876543210L);
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        customer = new Customer();
        customer.setIdentifier("CUST123");
        customer.setPhoneNo(9876543210L);
        customer.setDeleted(false);
    }

    @Test
    void testFindByIdentifier_Found() {
        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findByIdentifier("CUST123");

        assertNotNull(result);
        assertEquals("CUST123", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_NotFound() {
        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifier("CUST123");

        assertNull(result);
    }

    @Test
    void testSave_WhenCustomerAlreadyExists_Active() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenCustomerAlreadyExists_SoftDeleted() {
        customer.setDeleted(true);
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Please contact administrator"));
    }

    @Test
    void testSave_NewCustomer() {
        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertNotNull(result);
        verify(addressService, times(1)).save(customerDto.getBillingAddress());
        verify(addressService, times(1)).save(customerDto.getShippingAddress());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testSave_NewCustomer_NullAddresses() {
        customerDto.setBillingAddress(null);
        customerDto.setShippingAddress(null);

        when(customerRepository.findByIdentifier("CUST123")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        verify(addressService, never()).save(any());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testUpdate_CustomerNotFound() {
        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_CustomerFound() {
        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123")).thenReturn(customer);

        when(addressService.findByPhoneNoAndAddressType(9876543210L, "billingAddress"))
                .thenReturn(customerDto.getBillingAddress());
        when(addressService.findByPhoneNoAndAddressType(9876543210L, "shippingAddress"))
                .thenReturn(customerDto.getShippingAddress());

        CustomerDto result = customerService.update(customerDto);
        assertNotNull(result);
        verify(customerRepository, times(1)).save(customer);
        verify(addressService, times(1)).update(customerDto.getBillingAddress());
        verify(addressService, times(1)).update(customerDto.getShippingAddress());
    }

    @Test
    void testUpdate_CustomerFound_NullAddresses() {
        customerDto.setBillingAddress(null);
        customerDto.setShippingAddress(null);

        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123")).thenReturn(customer);

        CustomerDto result = customerService.update(customerDto);
        assertNotNull(result);
        verify(addressService, times(2)).update(null);
    }

    @Test
    void testDelete_Success() {
        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123"))
                .thenReturn(customer);
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);
        doNothing().when(addressService).deleteByPhone(9876543210L);
        assertDoesNotThrow(() -> customerService.delete("CUST123"));
        verify(customerRepository, times(1))
                .findByIdentifierAndDeletedFalse("CUST123");
        verify(customerRepository, times(1))
                .save(customer);
        verify(addressService, times(1))
                .deleteByPhone(9876543210L);
        assertTrue(customer.getDeleted());
        assertNotNull(customer.getModifiedOn());
    }

    @Test
    void testDelete_CustomerNotFound() {
        when(customerRepository.findByIdentifierAndDeletedFalse("CUST123"))
                .thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> customerService.delete("CUST123")
        );

        assertTrue(exception.getMessage().contains("not found"));

        verify(customerRepository, times(1))
                .findByIdentifierAndDeletedFalse("CUST123");
        verify(customerRepository, never()).save(any());
        verify(addressService, never()).deleteByPhone(anyLong());
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> customerList = Collections.singletonList(customer);
        List<CustomerDto> customerDtoList = Collections.singletonList(customerDto);

        Page<Customer> customerPage = new PageImpl<>(customerList, pageable, 1);

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        when(customerRepository.findAllByDeletedFalse(pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(customerPage.getContent(), listType))
                .thenReturn(customerDtoList);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }
}
