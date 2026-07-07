package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private AddressDto billing;
    private AddressDto shipping;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdentifier("12345");
        customer.setPhoneNo("12345");
        customer.setStatus(true);

        billing = new AddressDto();
        shipping = new AddressDto();

        customerDto = new CustomerDto();
        customerDto.setPhoneNo("12345");
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);
    }

    @Test
    void findByIdentifier_NotFound() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);

        CustomerDto result = customerService.findByIdentifier("12345");

        assertNull(result);
    }

    @Test
    void findByIdentifier_Found() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findByPhoneNoAndAddressType("12345", "billing")).thenReturn(billing);
        when(addressService.findByPhoneNoAndAddressType("12345", "shipping")).thenReturn(shipping);

        CustomerDto result = customerService.findByIdentifier("12345");

        assertNotNull(result);
        assertEquals(billing, result.getBillingAddress());
        assertEquals(shipping, result.getShippingAddress());
    }

    @Test
    void save_AlreadyExists() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void save_NewCustomer() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(customerDto);

        verify(addressService, times(1)).save(billing);
        verify(addressService, times(1)).save(shipping);
        verify(customerRepository, times(1)).save(customer);

        assertEquals("12345", customer.getIdentifier());
    }

    @Test
    void save_SoftDeletedCustomer() {

        customer.setDeleted(true);

        when(customerRepository.findByIdentifier("12345"))
                .thenReturn(customer);

        CustomerDto result =
                customerService.save(customerDto);

        assertFalse(result.isSuccess());

        assertTrue(
                result.getMessage()
                        .contains("deleted")
        );

        verify(customerRepository, never())
                .save(any());
    }

    @Test
    void update_NotFound() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void update_Success() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);

        when(addressService.findByPhoneNoAndAddressType(anyString(), anyString()))
                .thenReturn(billing)
                .thenReturn(shipping);

        CustomerDto result = customerService.update(customerDto);

        verify(addressService, times(1)).update(billing);
        verify(addressService, times(1)).update(shipping);
        verify(customerRepository, times(1)).save(customer);

        assertNotNull(result);
    }

    @Test
    void updateStatus_NotFound() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(null);

        CustomerDto result = customerService.updateStatus("12345", true);

        assertFalse(result.isSuccess());
        assertEquals("Product not found", result.getMessage());
    }

    @Test
    void updateStatus_Success() {
        when(customerRepository.findByIdentifier("12345")).thenReturn(customer);

        CustomerDto result = customerService.updateStatus("12345", false);

        assertTrue(result.isSuccess());
        assertEquals("Status updated successfully", result.getMessage());
        assertFalse(customer.isStatus());
    }

    @Test
    void update_SoftDeletedCustomer() {

        customer.setDeleted(true);

        when(customerRepository.findByIdentifier("12345"))
                .thenReturn(customer);

        CustomerDto result =
                customerService.update(customerDto);

        assertFalse(result.isSuccess());

        assertTrue(
                result.getMessage()
                        .contains("deleted")
        );

        verify(customerRepository, never())
                .save(any());
    }

    @Test
    void deleteCustomer() {

        Customer customer = new Customer();
        customer.setIdentifier("12345");
        customer.setDeleted(false);

        when(customerRepository.findByIdentifier("12345"))
                .thenReturn(customer);

        doNothing().when(addressService).delete("12345");

        customerService.delete("12345");

        assertTrue(customer.isDeleted());

        verify(customerRepository).findByIdentifier("12345");
        verify(customerRepository).save(customer);
        verify(addressService).delete("12345");
    }

    @Test
    void findAllWithPageableTest() {

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, customers.size());

        when(customerRepository.findByIsDeletedFalse(pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(
                eq(customers),
                any(Type.class)
        )).thenReturn(customerDtos);

        PaginationResponseDto<CustomerDto> result =
                customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(
                "CUST1",
                result.getDtoList().get(0).getIdentifier()
        );
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable =
                PageRequest.of(0,5);

        Specification<Customer> specification =
                mock(Specification.class);

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST1");

        Page<Customer> page =
                new PageImpl<>(
                        List.of(customer),
                        pageable,
                        1
                );

        when(
                customerRepository.findAll(
                        eq(specification),
                        eq(pageable)
                )
        ).thenReturn(page);

        when(
                modelMapper.map(
                        eq(List.of(customer)),
                        any(Type.class)
                )
        ).thenReturn(List.of(dto));

        PaginationResponseDto<CustomerDto> response =
                customerService.findAll(
                        specification,
                        pageable
                );

        assertEquals(
                1,
                response.getDtoList().size()
        );

        assertEquals(
                1,
                response.getTotalRecords()
        );

        assertEquals(
                0,
                response.getPage()
        );
    }

    @Test
    void findAllTest() {

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customerPage =
                new PageImpl<>(customers, pageable, customers.size());

        when(customerRepository.findByIsDeletedFalse(pageable))
                .thenReturn(customerPage);

        when(modelMapper.map(
                eq(customers),
                any(Type.class)
        )).thenReturn(customerDtos);

        PaginationResponseDto<CustomerDto> result =
                customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(
                "CUST1",
                result.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void searchCustomer_NullQuery() {

        List<CustomerDto> response =
                customerService.searchCustomer(null);

        assertTrue(response.isEmpty());

        verify(customerRepository, never())
                .searchActiveCustomers(any());
    }

    @Test
    void searchCustomer_EmptyQuery() {

        List<CustomerDto> response =
                customerService.searchCustomer("   ");


        assertTrue(response.isEmpty());


        verify(customerRepository, never())
                .searchActiveCustomers(any());
    }

    @Test
    void searchCustomer_Success() {

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");


        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST1");


        when(
                customerRepository.searchActiveCustomers("CUS")
        ).thenReturn(
                List.of(customer)
        );


        when(
                modelMapper.map(
                        customer,
                        CustomerDto.class
                )
        ).thenReturn(dto);


        List<CustomerDto> response =
                customerService.searchCustomer("CUS");


        assertEquals(
                1,
                response.size()
        );


        assertEquals(
                "CUST1",
                response.get(0).getIdentifier()
        );
    }
}