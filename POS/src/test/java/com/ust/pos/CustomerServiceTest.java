package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("9999999999");
        CustomerDto dto = new CustomerDto();
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        Mockito.when(addressService.findByPhoneNoAndAddressType
                ("9999999999", "billing")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneNoAndAddressType
                ("9999999999", "shipping")).thenReturn(shipping);
        CustomerDto response = customerService.findByIdentifier("9999999999");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("9999999999", response.getPhoneNo());
        Assertions.assertEquals(billing, response.getBillingAddress());
        Assertions.assertEquals(shipping, response.getShippingAddress());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        CustomerDto response = customerService.findByIdentifier("9999999999");
        Assertions.assertNull(response);
    }

    @Test
    void saveSuccessTest() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9999999999");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());
        Customer customer = new Customer();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(customer);
        CustomerDto response = customerService.save(dto);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(addressService, Mockito.times(2)).save(any(AddressDto.class));
        Mockito.verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void saveDuplicateCustomerTest() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9999999999");
        Customer customer = new Customer();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    void saveSoftDeletedCustomerTest() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9999999999");
        Customer customer = new Customer();
        customer.setDeleted(true);
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        CustomerDto response = customerService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void updateSuccessTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());
        Customer existingCustomer = new Customer();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(existingCustomer);
        CustomerDto response = customerService.update(dto);
        Assertions.assertNotNull(response);
        Mockito.verify(addressService, Mockito.times(2)).update(any(AddressDto.class));
        Mockito.verify(customerRepository).save(existingCustomer);
    }

    @Test
    void updateFailureTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        CustomerDto response = customerService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
    }

    @Test
    void deleteSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("9999999999");
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        customerService.delete("9999999999");
        Assertions.assertTrue(customer.isDeleted());
        Mockito.verify(customerRepository).save(customer);
        Mockito.verify(addressService).delete("9999999999");
    }

    @Test
    void deleteCustomerNotFoundTest() {
        Mockito.when(customerRepository.findByIdentifier("9999999999"))
                .thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                        () -> customerService.delete("9999999999"));
        Assertions.assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Customer customer = new Customer();
        customer.setStatus(false);
        CustomerDto dto = new CustomerDto();
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);
        CustomerDto response = customerService.toggleStatus("9999999999", true);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(customer.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);
        CustomerDto response = customerService.toggleStatus("9999999999", true);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer not found", response.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(any());
    }

    @Test
    void findAllWithPageableTest() {
        Customer customer = new Customer();
        customer.setIdentifier("9999999999");
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        List<Customer> customers = List.of(customer);
        List<CustomerDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> page = new PageImpl<>(customers);
        Mockito.when(customerRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<CustomerDto> response = customerService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("9999999999", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Customer customer = new Customer();
        customer.setIdentifier("9999999999");
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        List<Customer> customers = List.of(customer);
        List<CustomerDto> dtos = List.of(dto);
        Mockito.when(customerRepository.findAll()).thenReturn(customers);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<CustomerDto> response = customerService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("9999999999", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithSpecificationTest() {
        Customer customer = new Customer();
        customer.setIdentifier("9999999999");
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");
        List<Customer> customers = List.of(customer);
        List<CustomerDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> page = new PageImpl<>(customers, pageable, 1);
        Specification<Customer> specification = Mockito.mock(Specification.class);
        Mockito.when(customerRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<CustomerDto> response = customerService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("9999999999", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(customerRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Customer> specification = Mockito.mock(Specification.class);
        Page<Customer> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        Mockito.when(customerRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<CustomerDto> response = customerService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(customerRepository).findAll(specification, pageable);
    }
}