package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.AddressRepository;
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

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

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
        AddressDto addressDto = new AddressDto();
        List<AddressDto> addressDtoList = new ArrayList<>();
        addressDtoList.add(addressDto);
        addressDtoList.add(addressDto);
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        Mockito.when(addressService.findAllByPhoneNo("Admin")).thenReturn(addressDtoList);
        CustomerDto response = customerService.findByIdentifierWithAddressDto("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        Customer customer = new Customer();
        AddressDto addressDto1 = new AddressDto();
        AddressDto addressDto2 = new AddressDto();
        customerDto.setBillingAddress(addressDto1);
        customerDto.setShippingAddress(addressDto2);
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        Mockito.when(customerRepository.save(customer)).thenReturn(customer);
        Mockito.when(modelMapper.map(addressDto1, AddressDto.class)).thenReturn(addressDto1);
        Mockito.when(modelMapper.map(addressDto2, AddressDto.class)).thenReturn(addressDto2);
        Mockito.when(addressService.save(Mockito.any(AddressDto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CustomerDto response = customerService.save(customerDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(addressService).save(addressDto1);
        Mockito.verify(addressService).save(addressDto2);
    }

    @Test
    void SaveTestFailure() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("Admin");
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(existingCustomer);
        CustomerDto response = customerService.save(customerDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        CustomerDto customerDto = new CustomerDto();
        AddressDto addressDto1 = new AddressDto();
        AddressDto addressDto2 = new AddressDto();
        customerDto.setIdentifier("Admin");
        Customer existingCustomer = new Customer();
        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(addressDto1);
        addresses.add(addressDto2);
        customerDto.setBillingAddress(addressDto1);
        customerDto.setShippingAddress(addressDto2);
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(existingCustomer);
        Mockito.when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);
        Mockito.when(addressService.findAllByPhoneNo("Admin")).thenReturn(addresses);
        addressService.update(addressDto1);
        addressService.update(addressDto2);
        CustomerDto response = customerService.update(customerDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateFailureTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        Mockito.when(customerRepository.findByIdentifier("Admin")).thenReturn(null);
        CustomerDto response = customerService.update(customerDto);
        Assertions.assertFalse(response.isSuccess());
    }


    @Test
    void deleteTest() {
        Mockito.doNothing().when(customerRepository).deleteByIdentifier("Admin");
        addressService.delete("Admin");
        boolean response = customerService.delete("Admin");
        Assertions.assertEquals(true, response);
    }

    @Test
    void findAllTest() {
        Customer customer = new Customer();
        customer.setIdentifier("Admin");
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);
        Page<Customer> customerPage = new PageImpl<>(customers, PageRequest.of(0, 2), customers.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(java.lang.reflect.Type.class))).thenReturn(customerDtos);
        List<CustomerDto> response = customerService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }


    @Test
    void findByStatusTest() {
        Customer customer = new Customer();
        customer.setIdentifier("Admin");
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("Admin");
        List<Customer> customers = List.of(customer);
        List<CustomerDto> customerDtos = List.of(customerDto);
        Mockito.when(customerRepository.findByStatusIsTrue()).thenReturn(customers);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(java.lang.reflect.Type.class))).thenReturn(customerDtos);
        List<CustomerDto> response = customerService.findIfTrue();
        Assertions.assertEquals(1, response.size());
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

}