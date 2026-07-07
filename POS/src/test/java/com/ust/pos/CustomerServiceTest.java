package com.ust.pos;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void findByIdentifierSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("CUS001");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        when(customerRepository.findByIdentifierAndIsDeletedFalse("CUS001"))
                .thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        CustomerDto result = customerService.findByIdentifier("CUS001");

        assertNotNull(result);
        assertEquals("CUS001", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(customerRepository.findByIdentifierAndIsDeletedFalse("CUS001"))
                .thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findByIdentifier("CUS001");
        });
    }

    @Test
    void saveSuccessTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");
        dto.setPhoneNo(9876543210L);

        Customer customer = new Customer();
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        when(modelMapper.map(any(CustomerDto.class), eq(Customer.class)))
                .thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        assertNotNull(result);
        assertEquals("CUS001", result.getIdentifier());
        verify(addressService, times(2)).save(any(AddressDto.class));
        verify(customerRepository).save(customer);
    }

    @Test
    void saveFailureTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        Customer existingCustomer = new Customer();

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Customer with identifier - CUS001 already exists", result.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateSuccessTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");
        dto.setPhoneNo(9876543210L);

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUS001");
        existingCustomer.setPhoneNo(9876543210L);
        existingCustomer.setDeleted(false);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(existingCustomer);

        when(addressService.findByPhoneNoAndAddressType(9876543210L, "billingAddress"))
                .thenReturn(new AddressDto());
        when(addressService.findByPhoneNoAndAddressType(9876543210L, "shippingAddress"))
                .thenReturn(new AddressDto());

        CustomerDto result = customerService.update(dto);

        assertNotNull(result);
        assertEquals("CUS001", result.getIdentifier());
        verify(addressService, times(2)).save(any(AddressDto.class));
        verify(modelMapper).map(dto, existingCustomer);
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    void updateNotFoundFailureTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        CustomerDto result = customerService.update(dto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Customer with identifier - CUS001 not found", result.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void updateDeletedFailureTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUS001");

        Customer existingCustomer = new Customer();
        existingCustomer.setDeleted(true);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.update(dto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Customer with identifier - CUS001 was deleted, Please Contact the Administrator to add.", result.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(customer);

        customerService.delete("CUS001");

        verify(customerRepository).findByIdentifier("CUS001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Customer> customers = List.of(new Customer(), new Customer());
        Page<Customer> page = new PageImpl<>(customers, pageable, 2);
        List<CustomerDto> dtoList = List.of(new CustomerDto(), new CustomerDto());

        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();

        when(customerRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        when(modelMapper.map(customers, listType))
                .thenReturn(dtoList);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getDtoList().size());
        assertEquals(2, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Customer> spec = mock(Specification.class);
        List<Customer> customers = List.of(new Customer());
        Page<Customer> page = new PageImpl<>(customers, pageable, 1);
        List<CustomerDto> dtoList = List.of(new CustomerDto());

        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();

        when(customerRepository.findAll(spec, pageable))
                .thenReturn(page);
        when(modelMapper.map(customers, listType))
                .thenReturn(dtoList);

        WsDto<CustomerDto> result = customerService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
    }

    @Test
    void buildAddressIdentifierTest() {
        AddressDto address = new AddressDto();
        address.setAddressline("  Main Street ");
        address.setZipcode(500001L);
        address.setAddressType("billingAddress");

        String result = customerService.buildAddressIdentifier(address);

        assertEquals("MAIN STREET-500001-BILLINGADDRESS", result);
    }

    @Test
    void buildAddressIdentifierNullTest() {
        assertNull(customerService.buildAddressIdentifier(null));
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Customer customer = new Customer();
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(customer);

        customerService.toggleStatus("CUS001");

        assertFalse(customer.getStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Customer customer = new Customer();
        customer.setStatus(false);

        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(customer);

        customerService.toggleStatus("CUS001");

        assertTrue(customer.getStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusCustomerNotFoundTest() {
        when(customerRepository.findByIdentifier("CUS001"))
                .thenReturn(null);

        customerService.toggleStatus("CUS001");

        verify(customerRepository, never()).save(any(Customer.class));
    }
}