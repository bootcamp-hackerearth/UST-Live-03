package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressServiceImpl addressService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void findByIdentifierTest() {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.findByIdentifier("CUST01");

        Assertions.assertEquals("CUST01", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestFailure() {
        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findByIdentifier("CUST01");
        });
    }

    @Test
    void findByIdentifierWithAddressDtoTest() {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        List<AddressDto> addressDtoList = List.of(billing, shipping);

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        Mockito.when(addressService.findAllByPhoneNo("CUST01")).thenReturn(addressDtoList);

        CustomerDto response = customerService.findByIdentifierWithAddressDto("CUST01");

        Assertions.assertEquals("CUST01", response.getIdentifier());
        Assertions.assertEquals(billing, response.getBillingAddress());
        Assertions.assertEquals(shipping, response.getShippingAddress());
    }

    @Test
    void findByIdentifierWithAddressDtoTestFailure() {
        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findByIdentifierWithAddressDto("CUST01");
        });
    }

    @Test
    void saveTestSuccess() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(null);
        Customer customer = new Customer();
        Mockito.when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(addressService.save(Mockito.any(AddressDto.class))).thenReturn(billing);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertEquals("CUST01", response.getIdentifier());
        Mockito.verify(addressService, Mockito.times(2)).save(Mockito.any(AddressDto.class));
    }

    @Test
    void saveTestFailureAlreadyExists() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST01");
        existingCustomer.setDeleted(false);

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(existingCustomer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST01 already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST01");
        existingCustomer.setDeleted(true);

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(existingCustomer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer with identifier CUST01 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST01");

        AddressDto oldBilling = new AddressDto();
        oldBilling.setIdentifier("CUST01_Billing");
        AddressDto oldShipping = new AddressDto();
        oldShipping.setIdentifier("CUST01_Shipping");
        List<AddressDto> addresses = List.of(oldBilling, oldShipping);

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(existingCustomer);
        Mockito.when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);
        Mockito.when(addressService.findAllByPhoneNo("CUST01")).thenReturn(addresses);
        Mockito.when(addressService.update(Mockito.any(AddressDto.class))).thenReturn(billing);

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertEquals("CUST01", response.getIdentifier());
        Mockito.verify(addressService, Mockito.times(2)).update(Mockito.any(AddressDto.class));
    }

    @Test
    void updateTestFailure() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(null);

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST01 not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(addressService.delete("CUST01")).thenReturn(true);

        boolean response = customerService.delete("CUST01");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(null);

        boolean response = customerService.delete("CUST01");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Customer customer = new Customer();
        List<Customer> customers = List.of(customer);
        Page<Customer> customerPage = new PageImpl<>(customers, pageable, customers.size());

        CustomerDto customerDto = new CustomerDto();
        List<CustomerDto> customerDtos = List.of(customerDto);

        Mockito.when(customerRepository.findByDeletedFalse(pageable)).thenReturn(customerPage);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(java.lang.reflect.Type.class))).thenReturn(customerDtos);

        WsDto<CustomerDto> response = customerService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusTest() {
        Customer customer = new Customer();
        customer.setStatus(false);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setStatus(true);

        Mockito.when(customerRepository.findByIdentifier("CUST01")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.toggleStatus("CUST01");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findIfTrueTest() {
        Customer customer = new Customer();
        List<Customer> customers = List.of(customer);
        CustomerDto customerDto = new CustomerDto();
        List<CustomerDto> customerDtos = List.of(customerDto);

        Mockito.when(customerRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(customers);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(java.lang.reflect.Type.class))).thenReturn(customerDtos);

        List<CustomerDto> response = customerService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByIdentifierAndDeletedFalseTest() {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST01");

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("CUST01")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.findByIdentifierAndDeletedFalse("CUST01");

        Assertions.assertEquals("CUST01", response.getIdentifier());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Customer> specification = Mockito.mock(Specification.class);
        Customer customer = new Customer();
        List<Customer> customers = List.of(customer);
        Page<Customer> page = new PageImpl<>(customers, pageable, customers.size());

        CustomerDto customerDto = new CustomerDto();
        List<CustomerDto> customerDtos = List.of(customerDto);

        Mockito.when(customerRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(java.lang.reflect.Type.class))).thenReturn(customerDtos);

        WsDto<CustomerDto> response = customerService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}