package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Customer;
import com.ust.pos.models.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
    void findByIdentifierAndAddressTest() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        assertNotNull(customerService.findByIdentifier("C001"));
        when(customerRepository.findByIdentifierAndDeletedFalse("C002")).thenReturn(null);
        assertNull(customerService.findByIdentifier("C002"));
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(customerDto.getBillingAddress(),customerDto.getShippingAddress()));
        CustomerDto result = customerService.findByIdentifierWithAddressDto("9999999999");
        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(null);
        result = customerService.findByIdentifierWithAddressDto("9999999999");
        assertNotNull(result);
    }

    @Test
    void saveTest() {
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(any(AddressDto.class), eq(AddressDto.class))).thenAnswer(i -> i.getArgument(0));
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(null);
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(null);
        CustomerDto result = customerService.save(customerDto);
        assertTrue(result.isSuccess());
        customerDto.setShippingAddress(null);
        result = customerService.save(customerDto);
        assertTrue(result.isSuccess());
        customerDto.setBillingAddress(null);
        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");
        customerDto.setShippingAddress(shipping);
        result = customerService.save(customerDto);
        assertTrue(result.isSuccess());
        Customer active = new Customer();
        active.setDeleted(false);
        when(customerRepository.findByPhoneNo("111")).thenReturn(active);
        CustomerDto dto1 = new CustomerDto();
        dto1.setPhoneNo("111");
        result = customerService.save(dto1);
        assertFalse(result.isSuccess());
        Customer deleted = new Customer();
        deleted.setDeleted(true);
        when(customerRepository.findByPhoneNo("222")).thenReturn(deleted);
        CustomerDto dto2 = new CustomerDto();
        dto2.setPhoneNo("222");
        result = customerService.save(dto2);
        assertFalse(result.isSuccess());
        when(customerRepository.findByPhoneNo("333")).thenReturn(null);
        when(customerRepository.findByIdentifierAndDeletedFalse("C333")).thenReturn(customer);
        CustomerDto dto3 = new CustomerDto();
        dto3.setIdentifier("C333");
        dto3.setPhoneNo("333");
        result = customerService.save(dto3);
        assertFalse(result.isSuccess());
    }

    @Test
    void updateTest() {
        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");
        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(billing, shipping));
        CustomerDto result = customerService.update(customerDto);
        assertTrue(result.isSuccess());
        when(customerRepository.findByIdentifierAndDeletedFalse("C002")).thenReturn(null);
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C002");
        result = customerService.update(dto);
        assertFalse(result.isSuccess());
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(null);
        result = customerService.update(customerDto);
        assertTrue(result.isSuccess());
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(new ArrayList<>());
        result = customerService.update(customerDto);
        assertTrue(result.isSuccess());
    }

    @Test
    void deleteTest() {
        when(customerRepository.findByPhoneNoAndDeletedFalse("9999999999")).thenReturn(customer);
        customerService.delete("9999999999");
        verify(customerRepository).save(customer);
        verify(addressService).delete("9999999999");
        when(customerRepository.findByPhoneNoAndDeletedFalse("111")).thenReturn(null);
        assertDoesNotThrow(() -> customerService.delete("111"));
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(customer));
        when(customerRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(customerDto));
        WsDto<CustomerDto> result = customerService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Customer> specification = mock(Specification.class);
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(customerRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(customerDto));
        WsDto<CustomerDto> result = customerService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        verify(customerRepository).findAll(specification, pageable);
    }

    @Test
    void toggleStatusTest() {
        when(customerRepository.findByIdentifierAndDeletedFalse("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        customer.setStatus(true);
        customerService.toggleStatus("C001");
        assertFalse(customer.getStatus());
        customer.setStatus(false);
        customerService.toggleStatus("C001");
        assertTrue(customer.getStatus());
        verify(customerRepository, atLeast(2)).save(customer);
    }

    @Test
    void findIfTrueTest() {
        when(customerRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(List.of(customer));
        when(modelMapper.map(anyList(), any(Type.class))).thenReturn(List.of(customerDto));
        List<CustomerDto> result = customerService.findIfTrue();
        assertEquals(1, result.size());
    }
}