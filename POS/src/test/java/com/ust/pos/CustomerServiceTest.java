package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Customer;
import com.ust.pos.modell.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setIdentifier("C001");
        customer.setPhoneNo("9999999999");
        customer.setStatus(true);
        customer.setDeleted(false);
        customerDto = new CustomerDto();
        customerDto.setIdentifier("C001");
        customerDto.setPhoneNo("9999999999");
        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");
        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);
    }

    @Test
    void findByIdentifier_success() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        CustomerDto result = customerService.findByIdentifier("C001");
        assertNotNull(result);
    }

    @Test
    void findByIdentifier_notFound() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(null);
        assertNull(customerService.findByIdentifier("C001"));
    }

    @Test
    void findWithAddress_success() {
        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");
        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(billing, shipping));
        CustomerDto result = customerService.findByIdentifierWithAddressDto("9999999999");
        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
    }

    @Test
    void save_success() {
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(null);
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(any(AddressDto.class), eq(AddressDto.class))).thenAnswer(inv -> inv.getArgument(0));
        CustomerDto result = customerService.save(customerDto);
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
        assertTrue(result.isSuccess());
    }

    @Test
    void save_phoneExists_notDeleted() {
        Customer existing = new Customer();
        existing.setDeleted(false);
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(existing);
        CustomerDto result = customerService.save(customerDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void save_phoneExists_deleted() {
        Customer existing = new Customer();
        existing.setDeleted(true);
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(existing);
        CustomerDto result = customerService.save(customerDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void save_identifierExists() {
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(null);
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        CustomerDto result = customerService.save(customerDto);
        assertFalse(result.isSuccess());
    }

    @Test
    void update_success() {
        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");
        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(billing, shipping));
        CustomerDto result = customerService.update(customerDto);
        assertTrue(result.isSuccess());
        verify(addressService, times(2)).update(any(AddressDto.class));
        verify(customerRepository).save(customer);
    }

    @Test
    void update_notFound() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(null);
        CustomerDto result = customerService.update(customerDto);
        assertFalse(result.isSuccess());
    }

    @Test
    void update_nullAddresses_shouldCoverBranch() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(null);
        CustomerDto result = customerService.update(customerDto);
        assertTrue(result.isSuccess());
        verify(addressService, never()).update(any());
    }

    @Test
    void update_emptyAddresses_shouldCoverLoop() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(new ArrayList<>());
        CustomerDto result = customerService.update(customerDto);
        assertTrue(result.isSuccess());
    }

    @Test
    void delete_success() {
        when(customerRepository.findByPhoneNoAndDeletedFalse("9999999999")).thenReturn(customer);
        customerService.delete("9999999999");
        verify(customerRepository).save(customer);
        verify(addressService).delete("9999999999");
    }

    @Test
    void delete_notFound() {
        when(customerRepository.findByPhoneNoAndDeletedFalse("9999999999")).thenReturn(null);
        customerService.delete("9999999999");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(customer));
        when(customerRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(customerDto));
        WsDto<CustomerDto> result = customerService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void toggleStatus_success() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        boolean before = customer.getStatus();
        customerService.toggleStatus("C001");
        assertEquals(!before, customer.getStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void findIfTrue_success() {
        when(customerRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(List.of(customer));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(customerDto));
        List<CustomerDto> result = customerService.findIfTrue();
        assertEquals(1, result.size());
    }
}