package com.ust.pos;

import com.ust.pos.address.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PageDto;
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

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(modelMapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        Mockito.when(addressService.findByPhoneAndAddressType(
                        phone,
                        CustomerServiceImpl.BILLING))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByPhoneAndAddressType(
                        phone,
                        CustomerServiceImpl.SHIPPING))
                .thenReturn(new AddressDto());

        CustomerDto result = customerService.findByIdentifier(phone);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(phone, result.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        CustomerDto result =
                customerService.findByIdentifier("123");

        Assertions.assertNull(result);
    }

    @Test
    void save_success() {

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
                .thenReturn(new AddressDto());

        CustomerDto result = customerService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(addressService, Mockito.times(2))
                .save(Mockito.any(AddressDto.class));

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void save_duplicate() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        Customer existingCustomer = new Customer();
        existingCustomer.setDeleted(false);

        Mockito.when(customerRepository.findByIdentifier("1234567890"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Customer with phone number - 1234567890 already exists",
                result.getMessage());

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void save_softDeletedCustomer() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("1234567890");

        Customer existingCustomer = new Customer();
        existingCustomer.setDeleted(true);

        Mockito.when(customerRepository.findByIdentifier("1234567890"))
                .thenReturn(existingCustomer);

        CustomerDto result = customerService.save(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Customer with identifier - 1234567890 has been soft deleted. Restore it by changing status.",
                result.getMessage());

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void update_success() {

        String phone = "1234567890";

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(addressService.findByPhoneAndAddressType(
                        phone,
                        CustomerServiceImpl.BILLING))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByPhoneAndAddressType(
                        phone,
                        CustomerServiceImpl.SHIPPING))
                .thenReturn(new AddressDto());

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto result = customerService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(customerRepository)
                .save(customer);
    }
    @Test
    void update_withNullAddresses_createsNewAddressDtos() {

        String phone = "1234567890";

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);

        AddressDto billingAddress = new AddressDto();
        billingAddress.setAddressLine("123 Main St");
        dto.setBillingAddress(billingAddress);

        AddressDto shippingAddress = new AddressDto();
        shippingAddress.setAddressLine("456 Side St");
        dto.setShippingAddress(shippingAddress);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, CustomerServiceImpl.BILLING))
                .thenReturn(null);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, CustomerServiceImpl.SHIPPING))
                .thenReturn(null);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        Mockito.doNothing().when(modelMapper)
                .map(Mockito.any(AddressDto.class), Mockito.any(AddressDto.class));

        CustomerDto result = customerService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(modelMapper, Mockito.times(2))
                .map(Mockito.any(AddressDto.class), Mockito.any(AddressDto.class));

        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void update_withExistingAddresses_mapsIntoExistingAddressDtos() {

        String phone = "1234567890";

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);

        AddressDto billingAddress = new AddressDto();
        billingAddress.setAddressLine("123 Main St");
        dto.setBillingAddress(billingAddress);

        AddressDto shippingAddress = new AddressDto();
        shippingAddress.setAddressLine("456 Side St");
        dto.setShippingAddress(shippingAddress);

        Customer customer = new Customer();

        AddressDto existingBilling = new AddressDto();
        existingBilling.setPhoneNo(phone);
        existingBilling.setAddressType(CustomerServiceImpl.BILLING);

        AddressDto existingShipping = new AddressDto();
        existingShipping.setPhoneNo(phone);
        existingShipping.setAddressType(CustomerServiceImpl.SHIPPING);

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, CustomerServiceImpl.BILLING))
                .thenReturn(existingBilling);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, CustomerServiceImpl.SHIPPING))
                .thenReturn(existingShipping);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto result = customerService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(modelMapper).map(billingAddress, existingBilling);
        Mockito.verify(modelMapper).map(shippingAddress, existingShipping);

        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void update_withNullBillingAndShippingInDto_skipsMapping() {

        String phone = "1234567890";

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier(phone);
        dto.setBillingAddress(null);
        dto.setShippingAddress(null);

        Customer customer = new Customer();

        Mockito.when(customerRepository.findByIdentifier(phone))
                .thenReturn(customer);

        Mockito.when(addressService.findByPhoneAndAddressType(phone, CustomerServiceImpl.BILLING))
                .thenReturn(new AddressDto());

        Mockito.when(addressService.findByPhoneAndAddressType(phone, CustomerServiceImpl.SHIPPING))
                .thenReturn(new AddressDto());

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerDto result = customerService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(modelMapper, Mockito.never())
                .map(Mockito.any(AddressDto.class), Mockito.any(AddressDto.class));

        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void update_notFound() {

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        CustomerDto result = customerService.update(dto);

        Assertions.assertFalse(result.isSuccess());

        Assertions.assertEquals(
                "Customer not found",
                result.getMessage());
    }

    @Test
    void delete_success() {

        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setDeleted(false);
        customer.setStatus(true);

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        boolean result = customerService.delete("123");

        Assertions.assertTrue(result);

        Mockito.verify(customerRepository)
                .save(customer);

        Mockito.verify(addressService)
                .delete("123");
    }

    @Test
    void delete_failure() {

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        boolean result = customerService.delete("123");

        Assertions.assertFalse(result);
    }

    @Test
    void delete_alreadyDeleted() {

        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setDeleted(true);

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        boolean result = customerService.delete("123");

        Assertions.assertFalse(result);

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());

        Mockito.verify(addressService, Mockito.never())
                .delete(Mockito.anyString());
    }

    @Test
    void findAll_pagination() {

        Customer customer = new Customer();
        customer.setIdentifier("123");

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> page =
                new PageImpl<>(List.of(customer), pageable, 1);

        Mockito.when(customerRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Type listType =
                new TypeToken<List<CustomerDto>>() {
                }.getType();

        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(dto));

        PageDto<CustomerDto> result =
                customerService.findAll(pageable);

        Assertions.assertEquals(
                1,
                result.getDtoList().size());

        Assertions.assertEquals(
                1,
                result.getTotalRecords());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                0,
                result.getPage());
    }

    @Test
    void toggleStatus_test() {

        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setStatus(true);

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(customer);

        Mockito.when(customerRepository.save(customer))
                .thenReturn(customer);

        customerService.toggleStatus("123");

        Assertions.assertFalse(customer.getStatus());

        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void toggleStatus_failure() {

        Mockito.when(customerRepository.findByIdentifier("123"))
                .thenReturn(null);

        customerService.toggleStatus("123");

        Mockito.verify(customerRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findActiveCustomers_test() {

        Customer customer = new Customer();
        customer.setIdentifier("123");
        customer.setStatus(true);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("123");

        List<Customer> list = List.of(customer);

        Type listType =
                new TypeToken<List<CustomerDto>>() {
                }.getType();

        Mockito.when(customerRepository.findByStatusTrue())
                .thenReturn(list);

        Mockito.when(modelMapper.map(list, listType))
                .thenReturn(List.of(dto));

        List<CustomerDto> result =
                customerService.findActiveCustomers();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("123", result.get(0).getIdentifier());
    }

    @Test
    void findActiveCustomers_empty() {

        Type listType =
                new TypeToken<List<CustomerDto>>() {
                }.getType();

        Mockito.when(customerRepository.findByStatusTrue())
                .thenReturn(List.of());

        Mockito.when(modelMapper.map(List.of(), listType))
                .thenReturn(List.of());

        List<CustomerDto> result =
                customerService.findActiveCustomers();

        Assertions.assertTrue(result.isEmpty());
    }
}