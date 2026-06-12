package com.ust.pos;

import com.ust.pos.address.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void findByIdentifier_success() {
        String phone = "1234567890";

        Customer customer = new Customer();
        customer.setIdentifier(phone);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);
        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);
        Mockito.when(addressService.findByPhoneAndAddressType(phone, "billing"))
                .thenReturn(billing);
        Mockito.when(addressService.findByPhoneAndAddressType(phone, "shipping"))
                .thenReturn(shipping);

        CustomerDto result = customerService.findByIdentifier(phone);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getBillingAddress());
        Assertions.assertNotNull(result.getShippingAddress());
        Assertions.assertEquals(phone, result.getIdentifier());
    }

    @Test
    void save_success() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Mockito.when(customerRepository.findByIdentifier("1234567890"))
                .thenReturn(null);

        Customer customer = new Customer();
        Mockito.when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        Mockito.when(addressService.save(Mockito.any(AddressDto.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CustomerDto result = customerService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(addressService, Mockito.times(2))
                .save(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void save_duplicateCustomer() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        Mockito.when(customerRepository.findByIdentifier("1234567890"))
                .thenReturn(new Customer());

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }
    @Test
    void save_success_whenBillingAndShippingAreNull() {
        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        Mockito.when(customerRepository.findByIdentifier("1234567890"))
                .thenReturn(null);

        Customer customer = new Customer();
        Mockito.when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        Mockito.when(addressService.save(Mockito.any(AddressDto.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CustomerDto result = customerService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertNotNull(result.getBillingAddress());
        Assertions.assertNotNull(result.getShippingAddress());

        Mockito.verify(addressService, Mockito.times(2))
                .save(Mockito.any(AddressDto.class));
    }

    @Test
    void findByIdentifier_customerNotFound() {
        String phone = "1234567890";

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(null);

        CustomerDto result = customerService.findByIdentifier(phone);

        Assertions.assertNull(result);

        Mockito.verify(customerRepository)
                .findByIdentifier(phone);
        Mockito.verifyNoInteractions(addressService);
    }


    @Test
    void update_success_withAddressesPresent() {
        String phone = "1234567890";

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Customer customer = new Customer();

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, "billing"))
                .thenReturn(billing);
        Mockito.when(addressService.findByPhoneAndAddressType(phone, "shipping"))
                .thenReturn(shipping);

        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.any(AddressDto.class), Mockito.any(AddressDto.class));

        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.any(CustomerDto.class), Mockito.eq(customer));

        Mockito.doNothing()
                .when(addressService)
                .update(Mockito.any(AddressDto.class));

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto result = customerService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(addressService, Mockito.times(2))
                .update(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void update_success_whenAddressesDoNotExist() {
        String phone = "1234567890";

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, "billing"))
                .thenReturn(null);
        Mockito.when(addressService.findByPhoneAndAddressType(phone, "shipping"))
                .thenReturn(null);

        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.any(AddressDto.class), Mockito.any(AddressDto.class));

        Mockito.doNothing()
                .when(modelMapper)
                .map(Mockito.any(CustomerDto.class), Mockito.eq(customer));

        Mockito.doNothing()
                .when(addressService)
                .update(Mockito.any(AddressDto.class));

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto result = customerService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(addressService, Mockito.times(2))
                .update(Mockito.any(AddressDto.class));
        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void update_customerNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("1234567890");

        Mockito.when(customerRepository.findByIdentifier("1234567890"))
                .thenReturn(null);

        CustomerDto result = customerService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }


    @Test
    void delete_success() {
        Mockito.doNothing()
                .when(customerRepository)
                .deleteByIdentifier("1234567890");

        Mockito.doNothing()
                .when(addressService)
                .delete("1234567890");

        customerService.delete("1234567890");

        Mockito.verify(customerRepository)
                .deleteByIdentifier("1234567890");
        Mockito.verify(addressService)
                .delete("1234567890");
    }


    @Test
    void findAll_pagination() {
        Customer customer = new Customer();
        CustomerDto dto = new CustomerDto();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page =
                new PageImpl<>(List.of(customer), pageable, 1);

        Mockito.when(customerRepository.findAll(pageable))
                .thenReturn(page);

        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        List<CustomerDto> result = customerService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
    }
}