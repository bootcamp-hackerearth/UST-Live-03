package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.modell.Customer;
import com.ust.pos.modell.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
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

        customerDto = new CustomerDto();
        customerDto.setIdentifier("C001");
        customerDto.setPhoneNo("9999999999");
        customerDto.setBillingAddress(new AddressDto());
        customerDto.setShippingAddress(new AddressDto());
    }

    @Test
    void findByIdTest() {

        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        CustomerDto result = customerService.findById("C001");
        assertNotNull(result);
        assertEquals("C001", result.getIdentifier());
    }

    @Test
    void findWithAddress_nullList() {

        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(null);
        CustomerDto result = customerService.findByIdentifierWithAddressDto("9999999999");
        assertNotNull(result);
    }

    @Test
    void findWithAddress_emptyList() {

        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(new ArrayList<>());
        assertThrows(IndexOutOfBoundsException.class, () -> customerService.findByIdentifierWithAddressDto("9999999999"));
    }

    @Test
    void findWithAddress_singleItem() {

        AddressDto addr = new AddressDto();
        CustomerDto localDto = new CustomerDto();
        localDto.setIdentifier("C001");
        localDto.setPhoneNo("9999999999");
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(localDto);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(addr));
        CustomerDto result = customerService.findByIdentifierWithAddressDto("9999999999");
        assertNull(result.getShippingAddress());
    }

    @Test
    void findWithAddress_multipleItems() {

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(billing, shipping));
        CustomerDto result = customerService.findByIdentifierWithAddressDto("9999999999");
        assertNotNull(result.getShippingAddress());
    }

    @Test
    void saveSuccess() {

        when(customerRepository.findById("C001")).thenReturn(null);
        when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);
        when(modelMapper.map(any(AddressDto.class), eq(AddressDto.class))).thenAnswer(inv -> inv.getArgument(0));
        CustomerDto result = customerService.save(customerDto);
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).save(any(AddressDto.class));
        assertEquals("C001", result.getIdentifier());
    }

    @Test
    void saveDuplicate() {

        when(customerRepository.findById("C001")).thenReturn(customer);
        CustomerDto result = customerService.save(customerDto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void updateSuccess() {

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();
        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9999999999")).thenReturn(List.of(billing, shipping));
        customerService.update(customerDto);
        verify(customerRepository).save(customer);
        verify(addressService, times(2)).update(any());

    }

    @Test
    void updateNotFound() {

        when(customerRepository.findByPhoneNo("9999999999")).thenReturn(null);
        CustomerDto result = customerService.update(customerDto);
        assertFalse(result.isSuccess());
    }

    @Test
    void deleteTest() {

        boolean result = customerService.delete("9999999999");
        verify(customerRepository).deleteByPhoneNo("9999999999");
        verify(addressService).delete("9999999999");
        assertTrue(result);
    }

    @Test
    void findAllTest() {

        Customer customer1 = new Customer();
        customer1.setIdentifier("Admin");
        CustomerDto customerDto1 = new CustomerDto();
        customerDto1.setIdentifier("Admin");
        List<Customer> customers = List.of(customer1);
        List<CustomerDto> customerDtos = List.of(customerDto1);
        Page<Customer> customerPage = new PageImpl<>(customers, PageRequest.of(0, 2), customers.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(java.lang.reflect.Type.class))).thenReturn(customerDtos);
        List<CustomerDto> response = customerService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {

        when(customerRepository.findById("C001")).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);
        boolean before = customer.getStatus();
        CustomerDto result = customerService.toggleStatus("C001");
        assertEquals(!before, customer.getStatus());
        verify(customerRepository).save(customer);
        assertNotNull(result);
    }

    @Test
    void findIfTrueTest() {

        customer.setStatus(true);
        customerDto.setStatus(true);
        when(customerRepository.findByStatusIsTrue()).thenReturn(List.of(customer));
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(customerDto));
        List<CustomerDto> result = customerService.findIfTrue();
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStatus());
    }
}