package com.ust.pos;

import com.ust.pos.address.service.AddressService;
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
class CustomerServiceTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AddressService addressService;

    @Test
    void saveTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBilling(billing);
        dto.setShipping(shipping);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Customer.class))
                .thenReturn(customer);

        CustomerDto response = customerService.save(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(addressService)
                .save(shipping, billing);

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void saveTestFailureWhenCustomerExists() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(new Customer());

        CustomerDto response = customerService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());

        Mockito.verify(addressService, Mockito.never())
                .save(Mockito.any(), Mockito.any());
    }

    @Test
    void updateTestSuccess() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBilling(billing);
        dto.setShipping(shipping);

        Customer existing = new Customer();
        Customer mapped = new Customer();

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(existing);

        Mockito.when(modelMapper.map(dto, Customer.class))
                .thenReturn(mapped);

        CustomerDto response = customerService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(customerRepository)
                .save(mapped);

        Mockito.verify(addressService)
                .update(shipping, billing);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(null);

        CustomerDto response = customerService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());

        Mockito.verify(addressService, Mockito.never())
                .update(Mockito.any(), Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Customer customer = new Customer();
        customer.setIdentifier("C1");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(customer);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        Mockito.when(addressService.findByIdentifierAndBilling("C1"))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByIdentifierAndShipping("C1"))
                .thenReturn(new AddressDto());

        CustomerDto response = customerService.findByIdentifier("C1");

        Assertions.assertEquals("C1", response.getIdentifier());
        Assertions.assertNotNull(response.getBilling());
        Assertions.assertNotNull(response.getShipping());
    }

    @Test
    void findAllTest() {
        List<Customer> customers = List.of(new Customer());
        List<CustomerDto> dtos = List.of(new CustomerDto());

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Mockito.when(customerRepository.findByDeletedFalse())
                .thenReturn(customers);

        Mockito.when(modelMapper.map(customers, listType))
                .thenReturn(dtos);

        List<CustomerDto> response = customerService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void deleteTest() {
        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifierAndDeletedFalse("C1"))
                .thenReturn(customer);

        customerService.deleteByIdentifier("C1");

        Assertions.assertTrue(customer.isDeleted());

        Mockito.verify(addressService)
                .delete("C1");

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void findAllWithPaginationShouldReturnCustomerDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setIdentifier("C1");

        Page<Customer> customerPage =
                new PageImpl<>(List.of(customer));

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(customerRepository.findByDeletedFalse(pageable))
                .thenReturn(customerPage);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        Mockito.when(addressService.findByIdentifierAndBilling("C1"))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByIdentifierAndShipping("C1"))
                .thenReturn(new AddressDto());

        Page<CustomerDto> response =
                customerService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "C1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(customerRepository)
                .findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchShouldReturnCustomerDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        Customer customer = new Customer();
        customer.setIdentifier("C1");

        Page<Customer> customerPage =
                new PageImpl<>(List.of(customer));

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("C1");

        Mockito.when(
                customerRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "C1",
                        pageable
                )
        ).thenReturn(customerPage);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        Mockito.when(addressService.findByIdentifierAndBilling("C1"))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByIdentifierAndShipping("C1"))
                .thenReturn(new AddressDto());

        Page<CustomerDto> response =
                customerService.findAll(pageable, "C1");

        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(
                "C1",
                response.getContent().get(0).getIdentifier()
        );

        Mockito.verify(customerRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        "C1",
                        pageable
                );
    }
}