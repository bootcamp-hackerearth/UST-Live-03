package com.ust.pos;

import com.ust.pos.address.service.AddressService;
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
import org.springframework.data.jpa.domain.Specification;

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
    void findByIdentifierTest() {

        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(customer, CustomerDto.class)
        ).thenReturn(customerDto);

        Mockito.when(
                addressService.findByPhoneAndAddressType(
                        "1234567890",
                        "billing"
                )
        ).thenReturn(billing);

        Mockito.when(
                addressService.findByPhoneAndAddressType(
                        "1234567890",
                        "shipping"
                )
        ).thenReturn(shipping);

        CustomerDto response =
                customerService.findByIdentifier(
                        "1234567890"
                );

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getBillingAddress());
        Assertions.assertNotNull(response.getShippingAddress());
    }

    @Test
    void findByIdentifierDeletedCustomerTest() {

        Customer customer = new Customer();
        customer.setDeleted(true);

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(customer);

        CustomerDto response =
                customerService.findByIdentifier(
                        "1234567890"
                );

        Assertions.assertNull(response);
    }

    @Test
    void saveTest() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setPhoneNo("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(null);

        Customer customer = new Customer();

        Mockito.when(
                modelMapper.map(
                        customerDto,
                        Customer.class
                )
        ).thenReturn(customer);

        CustomerDto response =
                customerService.save(customerDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Customer created successfully",
                response.getMessage()
        );

        Mockito.verify(addressService, Mockito.times(2))
                .save(Mockito.any(AddressDto.class));

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void saveTestAlreadyExists() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setPhoneNo("1234567890");

        Customer existing = new Customer();

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(existing);

        CustomerDto response =
                customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("already exists")
        );
    }

    @Test
    void saveTestSoftDeletedCustomer() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setPhoneNo("1234567890");

        Customer existing = new Customer();
        existing.setDeleted(true);

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(existing);

        CustomerDto response =
                customerService.save(customerDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("soft deleted")
        );
    }

    @Test
    void updateTest() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        Customer existingCustomer = new Customer();

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(existingCustomer);

        Mockito.when(
                addressService.findByPhoneAndAddressType(
                        "1234567890",
                        "billing"
                )
        ).thenReturn(billing);

        Mockito.when(
                addressService.findByPhoneAndAddressType(
                        "1234567890",
                        "shipping"
                )
        ).thenReturn(shipping);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Customer updated successfully",
                response.getMessage()
        );

        Mockito.verify(addressService, Mockito.times(2))
                .update(Mockito.any(AddressDto.class));

        Mockito.verify(customerRepository)
                .save(existingCustomer);
    }

    @Test
    void updateTestFailure() {

        CustomerDto customerDto = new CustomerDto();
        customerDto.setIdentifier("1234567890");

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(null);

        CustomerDto response =
                customerService.update(customerDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        Mockito.when(
                customerRepository.findByIdentifier("1234567890")
        ).thenReturn(customer);

        customerService.delete("1234567890");

        Mockito.verify(customerRepository)
                .save(customer);

        Mockito.verify(addressService)
                .delete("1234567890");
    }

    @Test
    void findAllTest() {

        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("1234567890");

        List<Customer> customers =
                List.of(customer);

        List<CustomerDto> dtos =
                List.of(dto);

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Customer> customerPage =
                new PageImpl<>(
                        customers,
                        pageable,
                        1
                );

        Mockito.when(
                customerRepository.findByDeletedFalse(pageable)
        ).thenReturn(customerPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(customers),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<CustomerDto> response =
                customerService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
    }

    @Test
    void toggleStatusSuccessTest() {

        Customer customer = new Customer();
        customer.setIdentifier("Admin");
        customer.setStatus(false);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("Admin");

        Mockito.when(
                customerRepository.findByIdentifier("Admin")
        ).thenReturn(customer);

        Mockito.when(
                modelMapper.map(
                        customer,
                        CustomerDto.class
                )
        ).thenReturn(dto);

        CustomerDto response =
                customerService.toggleStatus(
                        "Admin",
                        true
                );

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "Status updated successfully",
                response.getMessage()
        );

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void toggleStatusFailureTest() {

        Mockito.when(
                customerRepository.findByIdentifier("Admin")
        ).thenReturn(null);

        CustomerDto response =
                customerService.toggleStatus(
                        "Admin",
                        true
                );

        Assertions.assertFalse(
                response.isSuccess()
        );

        Assertions.assertEquals(
                "Customer not found",
                response.getMessage()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        Customer customer = new Customer();
        customer.setIdentifier("1234567890");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("1234567890");

        List<Customer> customers = List.of(customer);
        List<CustomerDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Customer> page = new PageImpl<>(
                customers,
                pageable,
                1
        );

        Specification<Customer> specification =
                Mockito.mock(Specification.class);

        Mockito.when(
                customerRepository.findAll(specification, pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(customers),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<CustomerDto> response =
                customerService.findAll(specification, pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "1234567890",
                response.getDtoList().get(0).getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );

        Mockito.verify(customerRepository)
                .findAll(specification, pageable);
    }
}