package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
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
        customer.setIdentifier("Admin");
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierWithAddressDtoTest() {
        Customer customer = new Customer();
        customer.setIdentifier("Admin");
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");

        AddressDto billing = new AddressDto();
        billing.setIdentifier("Billing_ID");
        AddressDto shipping = new AddressDto();
        shipping.setIdentifier("Shipping_ID");
        List<AddressDto> addressList = List.of(billing, shipping);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        Mockito.when(addressService.findAllByPhoneNo("Admin")).thenReturn(addressList);

        CustomerDto response = customerService.findByIdentifierWithAddressDto("Admin");

        Assertions.assertEquals("Billing_ID", response.getBillingAddress().getIdentifier());
        Assertions.assertEquals("Shipping_ID", response.getShippingAddress().getIdentifier());
    }

    @Test
    void saveTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(null);
        Customer customer = new Customer();
        Mockito.when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Mockito.verify(addressService, Mockito.times(2)).save(Mockito.any(AddressDto.class));
    }

    @Test
    void saveTestFailure() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("Admin");
        existingCustomer.setDeleted(false);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(existingCustomer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("Admin");
        existingCustomer.setDeleted(true);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(existingCustomer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("Admin");

        AddressDto oldBilling = new AddressDto();
        oldBilling.setIdentifier("Old_Bill");
        AddressDto oldShipping = new AddressDto();
        oldShipping.setIdentifier("Old_Ship");
        List<AddressDto> addressList = List.of(oldBilling, oldShipping);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(existingCustomer);
        Mockito.when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);
        Mockito.when(addressService.findAllByPhoneNo("Admin")).thenReturn(addressList);

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(addressService, Mockito.times(2)).update(Mockito.any(AddressDto.class));
    }

    @Test
    void updateTestFailure() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(null);

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();
        customer.setIdentifier("Admin");

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);

        boolean response = customerService.delete("Admin");

        Assertions.assertTrue(response);
        Mockito.verify(addressService).delete("Admin");
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = customerService.delete("Admin");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllTest() {
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
    }

    @Test
    void toggleTestActive() {
        Customer customer = new Customer();
        customer.setStatus(false);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setStatus(true);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.toggleStatus("Admin");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Customer customer = new Customer();
        customer.setStatus(true);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setStatus(false);

        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.toggleStatus("Admin");

        Assertions.assertFalse(response.isStatus());
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
        customer.setIdentifier("Admin");
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.findByIdentifierAndDeletedFalse("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }
}