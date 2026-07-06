package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Spy
    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;

    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setIdentifier("CUST1");
        customer.setCustomerName("John");
        customer.setStatus(true);
        customer.setDeleted(false);

        customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");
        customerDto.setUsername("john");
        customerDto.setCustomerName("John");

        billingAddress = new AddressDto();
        shippingAddress = new AddressDto();

        customerDto.setBillingAddress(billingAddress);
        customerDto.setShippingAddress(shippingAddress);
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page =
                new PageImpl<>(Collections.singletonList(customer));

        when(customerRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(customerDto));

        WsDto<CustomerDto> result =
                customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Customer> specification =
                mock(Specification.class);

        Page<Customer> page =
                new PageImpl<>(Collections.singletonList(customer));

        when(customerRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(customerDto));

        WsDto<CustomerDto> result =
                customerService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(customerRepository)
                .findAll(specification, pageable);
    }

    @Test
    void testSave_NewCustomer() {

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(null);

        when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertTrue(result.isSuccess());

        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
    }

    @Test
    void testSave_AlreadyExists() {

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        customer.setDeleted(true);

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testDelete() {

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        boolean result = customerService.delete("CUST1");

        assertTrue(result);
        assertTrue(customer.isDeleted());
        assertFalse(customer.isStatus());

        verify(customerRepository).save(customer);
    }

    @Test
    void testUpdateStatus() {

        customer.setStatus(true);

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        customerService.updateStatus("CUST1");

        assertFalse(customer.isStatus());

        verify(customerRepository).save(customer);
    }

    @Test
    void testFindAllActive() {

        when(customerRepository.findAllByStatus(true))
                .thenReturn(List.of(customer));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(customerDto));

        List<CustomerDto> result =
                customerService.findAllActive();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByIdentifier() {

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(customerDto);

        CustomerDto result =
                customerService.findByIdentifier("CUST1");

        assertNotNull(result);
        assertEquals("CUST1", result.getIdentifier());
    }

    @Test
    void testUpdate_Success() {

        AddressDto existingBilling = new AddressDto();
        existingBilling.setAddressType("billing");
        existingBilling.setIdentifier("BILL1");

        AddressDto existingShipping = new AddressDto();
        existingShipping.setAddressType("shipping");
        existingShipping.setIdentifier("SHIP1");

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        when(addressService.findAllByPhoneNumber("CUST1"))
                .thenReturn(List.of(existingBilling, existingShipping));

        doNothing().when(modelMapper)
                .map(customerDto, customer);

        CustomerDto result = customerService.update(customerDto);

        assertTrue(result.isSuccess());
        assertEquals("Customer updated successfully",
                result.getMessage());

        verify(customerRepository).save(customer);

        verify(addressService, times(2))
                .update(any(AddressDto.class));
    }

    @Test
    void testUpdate_NotFound() {

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testChangeToggleStatus() {

        when(customerRepository.findByIdentifier("CUST1"))
                .thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(customerDto);

        CustomerDto result =
                customerService.changeToggleStatus("CUST1", false);

        assertNotNull(result);
        assertFalse(customer.isStatus());

        verify(customerRepository).save(customer);
    }

    @Test
    void testFindActiveStatus() {

        Customer inactive = new Customer();
        inactive.setStatus(false);

        when(customerRepository.findAll())
                .thenReturn(List.of(customer, inactive));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(customerDto));

        List<CustomerDto> result =
                customerService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}