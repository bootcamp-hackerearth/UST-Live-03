package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "CUST-001";

        Customer customer = new Customer();
        customer.setIdentifier(identifier);

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier(identifier);

        when(customerRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(customerDto);

        CustomerDto result = customerService.findByIdentifier(identifier);

        assertNotNull(result);
        assertEquals(identifier, result.getIdentifier());

        verify(customerRepository).findByIdentifierAndDeletedFalse(identifier);
    }

    @Test
    void testFindByIdentifier_NotFound() {
        String identifier = "CUST-001";

        when(customerRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(null);

        CustomerDto result = customerService.findByIdentifier(identifier);

        assertNull(result);
        verify(customerRepository).findByIdentifierAndDeletedFalse(identifier);
    }

    @Test
    void testSave_Success() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");
        customerDto.setPhoneNumber(9876543210L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer customer = new Customer();
        customer.setIdentifier("CUST-001");

        when(customerRepository.findByIdentifier("CUST-001"))
                .thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class))
                .thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertTrue(result.isSuccess());
        assertEquals("Customer saved successfully", result.getMessage());
        assertEquals("Customer", result.getPartyType());
        assertEquals(0.0, result.getCreditLimit());
        assertEquals(0.0, result.getBalance());

        assertEquals("Billing", billing.getAddressType());
        assertEquals("Shipping", shipping.getAddressType());

        verify(customerRepository).save(customer);
        verify(addressService).save(billing);
        verify(addressService).save(shipping);
    }

    @Test
    void testSave_AlreadyExists() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST-001");
        existingCustomer.setDeleted(false);

        when(customerRepository.findByIdentifier("CUST-001"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertEquals("Customer with identifier - CUST-001 already exists", result.getMessage());

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testSave_DeletedCustomerExists() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST-001");
        existingCustomer.setDeleted(true);

        when(customerRepository.findByIdentifier("CUST-001"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Customer with identifier - CUST-001 was deleted and cannot be created again",
                result.getMessage()
        );

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdate_Success() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");
        customerDto.setPhoneNumber(9876543210L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST-001");

        when(customerRepository.findByIdentifierAndDeletedFalse("CUST-001"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.update(customerDto);

        assertTrue(result.isSuccess());
        assertEquals("Customer updated successfully", result.getMessage());

        assertEquals("Billing", billing.getAddressType());
        assertEquals("Shipping", shipping.getAddressType());

        verify(modelMapper).map(customerDto, existingCustomer);
        verify(customerRepository).save(existingCustomer);
        verify(addressService).update(billing);
        verify(addressService).update(shipping);
    }

    @Test
    void testUpdate_NotFound() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");

        when(customerRepository.findByIdentifierAndDeletedFalse("CUST-001"))
                .thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertEquals("Customer with identifier - CUST-001 not found", result.getMessage());

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "CUST-001";

        Customer customer = new Customer();
        customer.setIdentifier(identifier);
        customer.setPhoneNumber(9876543210L);
        customer.setDeleted(false);

        when(customerRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(customer);

        customerService.delete(identifier);

        assertTrue(customer.getDeleted());

        verify(customerRepository).findByIdentifierAndDeletedFalse(identifier);
        verify(customerRepository).save(customer);
        verify(addressService).deleteByPhoneNumber(9876543210L);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setIdentifier("CUST-001");

        List<Customer> customerList = List.of(customer);
        Page<Customer> customerPage = new PageImpl<>(customerList, pageable, customerList.size());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST-001");

        List<CustomerDto> dtoList = List.of(customerDto);

        when(customerRepository.findAllByDeletedFalse(pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertNotNull(result.getDtoList());
        assertEquals(1, result.getDtoList().size());
        assertEquals("CUST-001", result.getDtoList().get(0).getIdentifier());

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(customerRepository).findAllByDeletedFalse(pageable);
        verify(modelMapper).map(anyList(), any(Type.class));
    }
    @Test
    void findAll_ShouldReturnWsDtoWithCustomerDtos() {
        Specification<Customer> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setIdentifier("CUS001");
        customer.setCustomerName("John");

        List<Customer> customerList = List.of(customer);
        Page<Customer> customerPage =
                new PageImpl<>(customerList, pageable, customerList.size());

        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1L);
        customerDto.setIdentifier("CUS001");
        customerDto.setCustomerName("John");

        List<CustomerDto> customerDtoList = List.of(customerDto);

        when(customerRepository.findAll(specification, pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(eq(customerList), any(Type.class)))
                .thenReturn(customerDtoList);

        WsDto<CustomerDto> result = customerService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(customerDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(customerRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(customerList), any(Type.class));
    }

}
