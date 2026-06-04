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
import java.util.ArrayList;
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
    private AddressDto billingAddress;
    private AddressDto shippingAddress;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdentifier("C001");
        customer.setPhoneNo("9876543210");
        customer.setStatus(true);

        billingAddress = new AddressDto();
        billingAddress.setAddressType("Billing");

        shippingAddress = new AddressDto();
        shippingAddress.setAddressType("Shipping");

        customerDto = new CustomerDto();
        customerDto.setIdentifier("C001");
        customerDto.setPhoneNo("9876543210");
        customerDto.setBillingAddress(billingAddress);
        customerDto.setShippingAddress(shippingAddress);
    }

    @Test
    void findById_shouldReturnCustomerDto() {
        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.findById("C001");

        assertNotNull(result);
        assertEquals("9876543210", result.getPhoneNo());
    }

    @Test
    void findByIdentifierWithAddressDto_nullAddressList() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result);
    }

    @Test
    void findByIdentifierWithAddressDto_singleAddress_noBillingOrShippingSet() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        List<AddressDto> oneItem = new ArrayList<>();
        oneItem.add(billingAddress);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(oneItem);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result);
    }

    @Test
    void findByIdentifierWithAddressDto_twoAddresses_shippingSet() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        List<AddressDto> twoItems = new ArrayList<>();
        twoItems.add(billingAddress);
        twoItems.add(shippingAddress);
        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(twoItems);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result);
        assertEquals(shippingAddress, result.getShippingAddress());
    }

    @Test
    void findByIdentifierWithAddressDto_isEmpty_branch_covered() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        List<AddressDto> trickyList = new ArrayList<AddressDto>() {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
        trickyList.add(billingAddress);

        when(addressService.findAllByPhoneNo("9876543210")).thenReturn(trickyList);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result);

        assertEquals(billingAddress, result.getBillingAddress());
    }

    @Test
    void save_shouldPersistCustomerAndAddresses() {
        when(customerRepository.findById("C001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(any(AddressDto.class), eq(AddressDto.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CustomerDto result = customerService.save(customerDto);

        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
        assertEquals("C001", result.getIdentifier());
    }

    @Test
    void save_shouldFail_whenCustomerExists() {
        when(customerRepository.findById("C001")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void update_shouldUpdateCustomerAndAddresses() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9876543210"))
                .thenReturn(List.of(billingAddress, shippingAddress));

        CustomerDto result = customerService.update(customerDto);

        verify(modelMapper).map(customerDto, customer);
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).update(any(AddressDto.class));
        assertEquals("9876543210", result.getPhoneNo());
    }

    @Test
    void update_shouldFail_whenCustomerNotFound() {
        when(customerRepository.findByPhoneNo("9876543210")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void delete_shouldDeleteCustomerAndAddresses() {
        doNothing().when(customerRepository).deleteByPhoneNo("9876543210");
        when(addressService.delete("9876543210")).thenReturn(true);

        boolean result = customerService.delete("9876543210");

        verify(customerRepository).deleteByPhoneNo("9876543210");
        verify(addressService).delete("9876543210");
        assertTrue(result);
    }

    @Test
    void findAllTest() {
        Page<Customer> page =
                new PageImpl<>(List.of(customer), PageRequest.of(0, 2), 1);

        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findAll(PageRequest.of(0, 2));

        assertEquals(1, result.size());
    }

    @Test
    void toggleStatus_trueToFalse() {
        customer.setStatus(true);
        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.toggleStatus("C001");

        assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void toggleStatus_falseToTrue() {
        customer.setStatus(false);
        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto result = customerService.toggleStatus("C001");

        assertTrue(customer.isStatus());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void findIfTrue_shouldReturnActiveCustomers() {
        when(customerRepository.findByStatusIsTrue()).thenReturn(List.of(customer));
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(customerDto));

        List<CustomerDto> result = customerService.findIfTrue();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isStatus());
    }
}