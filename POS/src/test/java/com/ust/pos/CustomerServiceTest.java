package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Address;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private CartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Address billing = new Address();
        billing.setZipcode(Long.valueOf("12345"));
        Address shipping = new Address();
        shipping.setZipcode(Long.valueOf("67890"));

        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer customer = new Customer();
        customer.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifier("CUST1")).thenReturn(null);
        Mockito.when(modelMapper.map(customerDto, Customer.class)).thenReturn(customer);

        AddressDto billingDto = new AddressDto();
        billingDto.setZipcode(Long.valueOf("12345"));
        AddressDto shippingDto = new AddressDto();
        shippingDto.setZipcode(Long.valueOf("67890"));

        Mockito.when(modelMapper.map(billing, AddressDto.class)).thenReturn(billingDto);
        Mockito.when(modelMapper.map(shipping, AddressDto.class)).thenReturn(shippingDto);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertEquals("CUST1", response.getIdentifier());
        verify(customerRepository).save(customer);
        verify(addressService, Mockito.times(2)).save(Mockito.any(AddressDto.class));
        verify(cartService).save(Mockito.any(CartDto.class));
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Customer existingCustomer = new Customer();

        Mockito.when(customerRepository.findByIdentifier("CUST1")).thenReturn(existingCustomer);

        CustomerDto response = customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST1 already exists", response.getMessage());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessNewAddressesTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifier("CUST1")).thenReturn(existingCustomer);
        Mockito.when(addressService.findByPhoneNo("CUST1")).thenReturn(new ArrayList<>());

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertEquals("CUST1", response.getIdentifier());
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    void updateSuccessExistingAddressesTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Address billing = new Address();
        Address shipping = new Address();
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer existingCustomer = new Customer();

        AddressDto bDto = new AddressDto();
        bDto.setIdentifier("B_ID");
        bDto.setAddressType("billing");

        AddressDto sDto = new AddressDto();
        sDto.setIdentifier("S_ID");
        sDto.setAddressType("shipping");

        List<AddressDto> addressList = List.of(bDto, sDto);

        Mockito.when(customerRepository.findByIdentifier("CUST1")).thenReturn(existingCustomer);
        Mockito.when(addressService.findByPhoneNo("CUST1")).thenReturn(addressList);

        Mockito.doNothing().when(modelMapper).map(Mockito.any(CustomerDto.class), Mockito.any(Customer.class));

        Mockito.when(modelMapper.map(Mockito.any(Address.class), Mockito.eq(AddressDto.class)))
                .thenReturn(new AddressDto());

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CUST1", response.getIdentifier());
        verify(customerRepository).save(existingCustomer);
        verify(addressService, Mockito.times(2)).update(Mockito.any(AddressDto.class));
    }

    @Test
    void updateFailureNotFoundTest() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifier("CUST1")).thenReturn(null);

        CustomerDto response = customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Customer with identifier - CUST1 not found", response.getMessage());
    }

    @Test
    void deleteSuccessTest() {
        Customer customer = new Customer();
        Mockito.when(customerRepository.findByIdentifier("CUST1")).thenReturn(customer);

        customerService.delete("CUST1");

        verify(addressService).delete("CUST1");
    }

    @Test
    void findAllSuccessTest() {
        Customer customer = new Customer();
        List<Customer> customerList = List.of(customer);
        Page<Customer> page = new PageImpl<>(customerList);
        Pageable pageable = PageRequest.of(0, 10);

        CustomerDto dto = new CustomerDto();
        List<CustomerDto> dtoList = List.of(dto);

        Mockito.when(customerRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(customerList), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifierAndIsDeletedFalse("CUST1")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        CustomerDto response = customerService.findByIdentifier("CUST1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("CUST1", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(customerRepository.findByIdentifierAndIsDeletedFalse("CUST1")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findByIdentifier("CUST1");
        });
    }

    @Test
    void findByIdentifierWithAddressDtoSuccessTest() {
        Customer customer = new Customer();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("CUST1");

        Mockito.when(customerRepository.findByIdentifierAndIsDeletedFalse("CUST1")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(customerDto);

        AddressDto bDto = new AddressDto();
        AddressDto sDto = new AddressDto();
        List<AddressDto> addressList = List.of(bDto, sDto);

        Mockito.when(addressService.findByPhoneNo("CUST1")).thenReturn(addressList);
        Mockito.when(modelMapper.map(bDto, Address.class)).thenReturn(new Address());
        Mockito.when(modelMapper.map(sDto, Address.class)).thenReturn(new Address());

        CustomerDto response = customerService.findByIdentifierWithAddressDto("CUST1");

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBillingAddress());
        Assertions.assertNotNull(response.getShippingAddress());
    }
}