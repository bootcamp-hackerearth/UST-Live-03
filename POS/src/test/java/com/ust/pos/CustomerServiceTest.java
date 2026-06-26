package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
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

    @Test
    void save_success() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");

        Customer customer = new Customer();

        when(customerRepository.findByPhoneNo("9876543210"))
                .thenReturn(null);

        when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        CustomerDto response = customerService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Customer created successfully",
                response.getMessage());
    }

    @Test
    void save_failure_invalidPhone() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("123");

        CustomerDto response = customerService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Valid 10-digit phone required",
                response.getMessage());
    }

    @Test
    void save_failure_duplicate() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");

        when(customerRepository.findByPhoneNo("9876543210"))
                .thenReturn(new Customer());

        CustomerDto response = customerService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Customer already exists",
                response.getMessage());
    }

    @Test
    void find_success() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");
        customer.setIdentifier("9876543210");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9876543210");

        when(customerRepository.findByIdentifier("9876543210"))
                .thenReturn(customer);

        when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        when(addressService.findByPhoneNoAndAddressType(
                "9876543210", "billing"))
                .thenReturn(new AddressDto());

        when(addressService.findByPhoneNoAndAddressType(
                "9876543210", "shipping"))
                .thenReturn(new AddressDto());

        CustomerDto response =
                customerService.findByIdentifier("9876543210");

        assertNotNull(response);
        assertEquals("9876543210",
                response.getIdentifier());
    }

    @Test
    void find_notFound() {

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        CustomerDto response =
                customerService.findByIdentifier("123");

        assertFalse(response.isSuccess());
        assertEquals("Customer not found",
                response.getMessage());
    }

    @Test
    void find_deletedCustomer() {

        Customer customer = new Customer();
        customer.setDeleted(true);

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        CustomerDto response =
                customerService.findByIdentifier("123");

        assertFalse(response.isSuccess());
        assertEquals("Customer not found",
                response.getMessage());
    }

    @Test
    void update_success() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9876543210");

        Customer customer = new Customer();
        customer.setIdentifier("9876543210");

        when(customerRepository.findByIdentifier("9876543210"))
                .thenReturn(customer);

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);

        CustomerDto response = customerService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Customer updated successfully",
                response.getMessage());
    }

    @Test
    void update_notFound() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        CustomerDto response = customerService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Customer not found",
                response.getMessage());
    }

    @Test
    void update_deletedCustomer() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        Customer customer = new Customer();
        customer.setDeleted(true);

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        CustomerDto response = customerService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Customer not found",
                response.getMessage());
    }

    @Test
    void delete_success() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        customerService.delete("123");

        assertTrue(customer.getDeleted());

        verify(customerRepository)
                .save(customer);

        verify(addressService)
                .delete("9876543210");
    }

    @Test
    void delete_notFound() {

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        customerService.delete("123");

        verify(customerRepository, never())
                .save(any());
    }

    @Test
    void findAll_success() {

        Customer customer = new Customer();

        List<Customer> customers = List.of(customer);

        Page<Customer> page =
                new PageImpl<>(customers);

        Pageable pageable =
                PageRequest.of(0, 5);

        List<CustomerDto> dtoList =
                List.of(new CustomerDto());

        when(customerRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<CustomerDto> result =
                customerService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1,
                result.getDtoList().size());
    }

    @Test
    void active_success() {

        Customer customer = new Customer();

        when(customerRepository
                .findByStatusTrueAndDeletedFalse())
                .thenReturn(List.of(customer));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(new CustomerDto()));

        List<CustomerDto> result =
                customerService.findActive();

        assertEquals(1, result.size());
    }

    @Test
    void toggle_success() {

        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setStatus(true);

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto response =
                customerService.toggleStatus("123");

        assertTrue(response.isSuccess());
        assertEquals("Status updated",
                response.getMessage());

        assertFalse(customer.getStatus());
    }

    @Test
    void toggle_notFound() {

        when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        CustomerDto response =
                customerService.toggleStatus("123");

        assertFalse(response.isSuccess());
        assertEquals("Customer not found",
                response.getMessage());
    }

    @Test
    void searchCustomer_success() {

        Customer customer = new Customer();

        when(customerRepository.searchActiveCustomers("john"))
                .thenReturn(List.of(customer));

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(new CustomerDto()));

        List<CustomerDto> result =
                customerService.searchCustomer("john");

        assertEquals(1, result.size());
    }

    @Test
    void searchCustomer_blankQuery() {

        List<CustomerDto> result =
                customerService.searchCustomer("");

        assertTrue(result.isEmpty());

        verify(customerRepository, never())
                .searchActiveCustomers(anyString());
    }
}