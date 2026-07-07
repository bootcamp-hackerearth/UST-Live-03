package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

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
    private AddressRepository addressRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CartEntryService cartEntryService;

    @Test
    void findByIdentifierSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = customerService.findByIdentifier("C1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.findByIdentifier("C1"));
    }

    @Test
    void saveSuccessTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(1234567890L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Customer.class)).thenReturn(customer);

        CustomerDto result = customerService.save(dto);

        verify(addressService).save(billing);
        verify(addressService).save(shipping);
        verify(customerRepository).save(customer);
        verify(cartService).save(any(CartDto.class));

        Assertions.assertEquals("C1", result.getIdentifier());
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Customer existing = new Customer();
        existing.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(existing);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        Customer existing = new Customer();
        existing.setIdentifier("C1");
        existing.setDeleted(true);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(existing);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Customer identifier - C1 not available", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");
        dto.setPhoneNo(1234567890L);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);
        Mockito.when(addressService.findByPhoneNoAndAddressType(1234567890L, "billingAddress")).thenReturn(billing);
        Mockito.when(addressService.findByPhoneNoAndAddressType(1234567890L, "shippingAddress")).thenReturn(shipping);

        CustomerDto result = customerService.update(dto);

        verify(addressService).update(billing);
        verify(addressService).update(shipping);
        verify(customerRepository).save(customer);
        verify(modelMapper).map(dto, customer);

        Assertions.assertNotNull(result.getBillingAddress());
        Assertions.assertNotNull(result.getShippingAddress());
    }

    @Test
    void updateFailureTest() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(null);

        CustomerDto result = customerService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");
        customer.setPhoneNo(9999999999L);

        Mockito.when(customerRepository.findByIdentifier("C1")).thenReturn(customer);

        customerService.delete("C1");

        verify(cartEntryService).deleteAllByCart("9999999999");
        verify(cartService).delete("9999999999");
        verify(addressRepository).deleteByPhoneNo(9999999999L);
        Assertions.assertTrue(customer.isDeleted());
    }

    @Test
    void findAllTest() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();

        List<Customer> customers = List.of(customer);
        List<CustomerDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(customers, pageable, customers.size());

        Mockito.when(customerRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());

        verify(customerRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllWithSpecificationTest() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();

        List<Customer> customers = List.of(customer);
        List<CustomerDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(customers, pageable, customers.size());

        Specification<Customer> spec = Mockito.mock(Specification.class);

        Mockito.when(customerRepository.findAll(spec, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<CustomerDto> result = customerService.findAll(spec, pageable, "keyword");

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("keyword", result.getKeyword());

        verify(customerRepository).findAll(spec, pageable);
    }

    @Test
    void buildAddressIdentifierTest() {
        AddressDto address = new AddressDto();
        address.setAddressLine("MG Road");
        address.setZipcode(560001L);
        address.setAddressType("billingAddress");

        String result = customerService.buildAddressIdentifier(address);

        Assertions.assertEquals("MG ROAD-560001-BILLINGADDRESS", result);
    }

    @Test
    void buildAddressIdentifierNullTest() {
        String result = customerService.buildAddressIdentifier(null);
        Assertions.assertNull(result);
    }

    @Test
    void deleteCustomerNotFoundTest() {
        Mockito.when(customerRepository.findByIdentifier("UNKNOWN")).thenReturn(null);

        customerService.delete("UNKNOWN");

        Mockito.verify(cartEntryService, Mockito.never()).deleteAllByCart(Mockito.anyString());
        Mockito.verify(cartService, Mockito.never()).delete(Mockito.anyString());
        Mockito.verify(addressRepository, Mockito.never()).deleteByPhoneNo(Mockito.anyLong());
    }

    @Test
    void constructorTest() {
        CustomerServiceImpl service = new CustomerServiceImpl(customerRepository, modelMapper, addressService, addressRepository, cartService, cartEntryService);
        Assertions.assertNotNull(service);
    }
}