package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
    void save_success() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("9999999999");
        customerDto.setBillingAddress(new Address());
        customerDto.setShippingAddress(new Address());

        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);

        Mockito.when(modelMapper.map(customerDto, Customer.class)).thenReturn(new Customer());

        Mockito.when(modelMapper.map(Mockito.any(Address.class), Mockito.eq(AddressDto.class))).thenReturn(new AddressDto());

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Mockito.verify(customerRepository).save(Mockito.any(Customer.class));
        Mockito.verify(addressService, Mockito.times(2))
                .save(Mockito.any(AddressDto.class));
    }

    @Test
    void save_failure_duplicateCustomer() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("9999999999");

        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(new Customer());

        CustomerDto result = customerService.save(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void update_success() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("9999999999");
        customerDto.setBillingAddress(new Address());
        customerDto.setShippingAddress(new Address());

        Customer existingCustomer = new Customer();
        existingCustomer.setIdentifier("9999999999");

        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(existingCustomer);

        AddressDto billingDto = new AddressDto();
        billingDto.setIdentifier("ADDR1");
        billingDto.setAddressType("billing");

        AddressDto shippingDto = new AddressDto();
        shippingDto.setIdentifier("ADDR2");
        shippingDto.setAddressType("shipping");

        Mockito.when(addressService.findByPhoneNo("9999999999")).thenReturn(List.of(billingDto, shippingDto));

        Mockito.when(modelMapper.map(Mockito.any(Address.class), Mockito.eq(AddressDto.class))).thenReturn(new AddressDto());

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertTrue(result.isSuccess());
        Mockito.verify(customerRepository).save(existingCustomer);
        Mockito.verify(addressService, Mockito.times(2)).update(Mockito.any(AddressDto.class));
    }

    @Test
    void update_failure_customerNotFound() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("9999999999");

        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(null);

        CustomerDto result = customerService.update(customerDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void delete_success() {
        customerService.delete("9999999999");

        Mockito.verify(customerRepository).deleteByIdentifier("9999999999");
        Mockito.verify(addressService).delete("9999999999");
    }

    @Test
    void findAll_success() {

        Customer c1 = new Customer();
        Customer c2 = new Customer();
        List<Customer> customers = List.of(c1, c2);

        CustomerDto d1 = new CustomerDto();
        CustomerDto d2 = new CustomerDto();
        List<CustomerDto> customerDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(customers);

        Mockito.when(customerRepository.findAll(pageable)).thenReturn(page);

        Mockito.when(modelMapper.map(Mockito.eq(customers), Mockito.any(Type.class))).thenReturn(customerDtos);

        List<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(customerDtos, result);
    }

    @Test
    void findByIdentifier_success() {

        Customer customer = new Customer();
        customer.setIdentifier("9999999999");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");

        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = customerService.findByIdentifier("9999999999");

        Assertions.assertEquals("9999999999", result.getIdentifier());
    }

    @Test
    void findByIdentifierWithAddressDto_success() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("9999999999");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(customerRepository.findByIdentifier("9999999999")).thenReturn(new Customer());
        Mockito.when(modelMapper.map(Mockito.any(Customer.class), Mockito.eq(CustomerDto.class))).thenReturn(dto);

        Mockito.when(addressService.findByPhoneNo("9999999999")).thenReturn(List.of(billing, shipping));

        Mockito.when(modelMapper.map(Mockito.any(AddressDto.class), Mockito.eq(Address.class))).thenReturn(new Address());

        CustomerDto result = customerService.findByIdentifierWithAddressDto("9999999999");

        Assertions.assertNotNull(result.getBillingAddress());
        Assertions.assertNotNull(result.getShippingAddress());
    }
}