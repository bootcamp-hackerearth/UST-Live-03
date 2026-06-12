package com.ust.pos;

import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.address.service.AddressService;
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
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;


    @Test
    void findAll_success() {

        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();
        Pageable pageable = Mockito.mock(Pageable.class);


        Page<Customer> page = new PageImpl<>(List.of(customer));

        Mockito.when(customerRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(customer)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void save_success_withAddresses() {

        CustomerDto input = new CustomerDto();
        input.setIdentifier("CUST01");
        input.setUsername("john");
        input.setCustomerName("John");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        input.setBillingAddress(billing);
        input.setShippingAddress(shipping);

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(null);

        Customer entity = new Customer();
        Mockito.when(modelMapper.map(input, Customer.class))
                .thenReturn(entity);

        Mockito.when(customerRepository.save(entity))
                .thenReturn(entity);

        Mockito.when(addressService.save(Mockito.any(AddressDto.class)))
                .thenReturn(new AddressDto());

        CustomerDto result = customerService.save(input);

        Assertions.assertTrue(result.isSuccess());
        Mockito.verify(addressService, Mockito.times(2))
                .save(Mockito.any(AddressDto.class));
    }

    @Test
    void save_failure_duplicate() {

        CustomerDto input = new CustomerDto();
        input.setIdentifier("CUST01");

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(new Customer());

        CustomerDto result = customerService.save(input);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(customerRepository).deleteByIdentifier("CUST01");

        Mockito.doNothing()
                .when(addressService).delete("CUST01");

        boolean result = customerService.delete("CUST01");

        Assertions.assertTrue(result);

        Mockito.verify(addressService)
                .delete("CUST01");
    }

    @Test
    void updateStatus_toggle() {

        Customer customer = new Customer();
        customer.setStatus(false);

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(customer);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        customerService.updateStatus("CUST01");

        Assertions.assertTrue(customer.isStatus());
    }

    @Test
    void findAllActive_success() {

        Customer customer = new Customer();
        customer.setStatus(true);

        CustomerDto dto = new CustomerDto();

        Mockito.when(customerRepository.findAllByStatus(true))
                .thenReturn(List.of(customer));

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(customer)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<CustomerDto> result = customerService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByIdentifier_success() {

        Customer customer = new Customer();
        customer.setIdentifier("CUST01");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST01");

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(customer);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        CustomerDto result =
                customerService.findByIdentifier("CUST01");

        Assertions.assertEquals("CUST01", result.getIdentifier());
    }

    @Test
    void update_success_withAddresses() {

        CustomerDto input = new CustomerDto();
        input.setIdentifier("CUST01");

        AddressDto billing = new AddressDto();
        billing.setAddressType("billing");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("shipping");

        input.setBillingAddress(billing);
        input.setShippingAddress(shipping);

        Customer existing = new Customer();

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(customerRepository.save(existing))
                .thenReturn(existing);

        AddressDto existingBilling = new AddressDto();
        existingBilling.setAddressType("billing");

        AddressDto existingShipping = new AddressDto();
        existingShipping.setAddressType("shipping");

        Mockito.when(addressService.findAllByPhoneNumber("CUST01"))
                .thenReturn(List.of(existingBilling, existingShipping));

        Mockito.when(addressService.update(Mockito.any(AddressDto.class)))
                .thenReturn(new AddressDto());

        CustomerDto result = customerService.update(input);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("Customer updated successfully",
                result.getMessage());
    }

    @Test
    void update_failure_notFound() {

        CustomerDto input = new CustomerDto();
        input.setIdentifier("CUST01");

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(null);

        CustomerDto result = customerService.update(input);

        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    void changeToggleStatus_success() {

        Customer customer = new Customer();
        customer.setStatus(false);

        CustomerDto dto = new CustomerDto();

        Mockito.when(customerRepository.findByIdentifier("CUST01"))
                .thenReturn(customer);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        CustomerDto result =
                customerService.changeToggleStatus("CUST01", true);

        Assertions.assertTrue(customer.isStatus());
        Assertions.assertNotNull(result);
    }
}