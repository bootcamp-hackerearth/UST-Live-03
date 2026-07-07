package com.ust.pos;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Address;
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

    @Mock
    private CartService cartService;

    @Test
    void findByIdentifierSuccessTest() {

        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);
        CustomerDto response =
                customerService.findByIdentifier("C1");
        assertNotNull(response);
    }

    @Test
    void findByIdentifierFailureTest() {

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(null);

        assertThrows(
                ResourseNotFoundException.class,
                () -> customerService.findByIdentifier("C1")
        );
    }

    @Test
    void saveSuccessWithBothAddressesTest() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(9999999999L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(null);
        when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        CustomerDto response =
                customerService.save(dto);

        assertEquals("C1", response.getIdentifier());
        verify(addressService, times(2))
                .save(any(AddressDto.class));
        verify(cartService).save(any(CartDto.class));
        verify(customerRepository).save(customer);

        assertEquals("billingAddress",
                billing.getAddressType());
        assertEquals("shippingAddress",
                shipping.getAddressType());
    }

    @Test
    void saveSuccessWithoutAddressesTest() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Customer customer = new Customer();

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(null);
        when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        customerService.save(dto);

        verify(addressService, never())
                .save(any());
        verify(cartService).save(any(CartDto.class));
        verify(customerRepository).save(customer);
    }

    @Test
    void saveFailureTest() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(new Customer());

        CustomerDto response =
                customerService.save(dto);

        assertFalse(response.isSuccess());

        verify(customerRepository, never())
                .save(any());
    }

    @Test
    void saveFailureDeletedCustomerTest() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Customer existing = new Customer();
        existing.setDeleted(true);

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(existing);

        CustomerDto response =
                customerService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(response.getMessage()
                .contains("already exists but was deleted"));
    }

    @Test
    void updateSuccessTest() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(9999999999L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer existing = new Customer();
        existing.setPhoneNo(9999999999L);

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(existing);

        when(addressService.findByPhoneNoAndAddressType(
                9999999999L, "billingAddress"))
                .thenReturn(new AddressDto());

        when(addressService.findByPhoneNoAndAddressType(
                9999999999L, "shippingAddress"))
                .thenReturn(new AddressDto());

        CustomerDto response =
                customerService.update(dto);

        assertEquals("C1", response.getIdentifier());

        verify(addressService).update(billing);
        verify(addressService).update(shipping);

        verify(customerRepository).save(existing);
    }

    @Test
    void updateFailureTest() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(null);

        CustomerDto response =
                customerService.update(dto);

        assertFalse(response.isSuccess());

        verify(customerRepository, never())
                .save(any());
    }

    @Test
    void deleteTest() {

        Customer customer = new Customer();

        Address address1 = new Address();
        Address address2 = new Address();

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(customer);

        when(addressService.findByPhoneNo(9999999999L))
                .thenReturn(List.of(address1, address2));

        customerService.delete("C1", 9999999999L);

        assertTrue(customer.isDeleted());
        assertTrue(address1.isDeleted());
        assertTrue(address2.isDeleted());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Page<Customer> page =
                new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);
        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        WsDto<CustomerDto> result =
                customerService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("C1",
                result.getContent().getFirst().getIdentifier());
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Customer customer = new Customer();
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(customer);
        customerService.toggleStatus("C1");

        assertFalse(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Customer customer = new Customer();
        customer.setStatus(false);

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(customer);
        customerService.toggleStatus("C1");

        assertTrue(customer.isStatus());
        verify(customerRepository).save(customer);
    }

    @Test
    void toggleStatusCustomerNotFoundTest() {

        when(customerRepository.findByIdentifier("C1"))
                .thenReturn(null);

        customerService.toggleStatus("C1");

        verify(customerRepository, never())
                .save(any());
    }

    @Test
    void findActiveCustomerTest() {

        Customer customer = new Customer();

        when(customerRepository.findByStatus(true))
                .thenReturn(List.of(customer));
        List<Customer> result =
                customerService.findActiveCustomer();

        assertEquals(1, result.size());
        verify(customerRepository).findByStatus(true);
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Customer> specification = mock(Specification.class);

        Customer customer = new Customer();
        customer.setIdentifier("CUST01");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        List<Customer> customerList = List.of(customer);

        Page<Customer> page = new PageImpl<>(customerList, pageable, 1);

        when(customerRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(customerList), any(Type.class)))
                .thenReturn(List.of(customerDto));

        WsDto<CustomerDto> result =
                customerService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("CUST01",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(customerRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(customerList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Customer> specification = mock(Specification.class);

        Page<Customer> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(customerRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<CustomerDto> result =
                customerService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(customerRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }
}