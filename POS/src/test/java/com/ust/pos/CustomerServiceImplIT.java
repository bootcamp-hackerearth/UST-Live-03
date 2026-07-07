package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.mongodb.assertions.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceImplIT {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @MockitoBean
    private AddressService addressService;

    @BeforeEach
    void setup() {
        customerRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateCustomer() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");
        dto.setCustomerName("Test Customer");

        CustomerDto result = customerService.save(dto);

        assertTrue(result.isSuccess());

        Customer saved =
                customerRepository.findByPhoneNo("9876543210");

        assertNotNull(saved);
        assertEquals("9876543210", saved.getPhoneNo());
    }

    @Test
    void save_ShouldFail_WhenPhoneNumberInvalid() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("12345");

        CustomerDto result = customerService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals("Invalid phone number", result.getMessage());
    }

    @Test
    void save_ShouldFail_WhenCustomerAlreadyExists() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");
        customer.setDeleted(false);

        customerRepository.save(customer);

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");

        CustomerDto result = customerService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Customer with phone - 9876543210 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldCreateAddresses() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");

        AddressDto billing = new AddressDto();
        AddressDto shipping = new AddressDto();

        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);

        customerService.save(dto);

        verify(addressService).save(
                argThat(a ->
                        "Billing".equals(a.getAddressType()) &&
                                "9876543210_Billing".equals(a.getIdentifier()))
        );

        verify(addressService).save(
                argThat(a ->
                        "Shipping".equals(a.getAddressType()) &&
                                "9876543210_Shipping".equals(a.getIdentifier()))
        );
    }

    @Test
    void update_ShouldUpdateCustomer() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");
        customer.setCustomerName("Old Name");
        customer.setDeleted(false);

        customerRepository.save(customer);

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9876543210");
        dto.setCustomerName("New Name");

        CustomerDto result = customerService.update(dto);

        assertTrue(result.isSuccess());

        Customer updated =
                customerRepository.findByPhoneNo("9876543210");

        assertEquals("New Name", updated.getCustomerName());
    }

    @Test
    void update_ShouldFail_WhenCustomerNotFound() {

        CustomerDto dto = new CustomerDto();
        dto.setPhoneNo("9999999999");

        CustomerDto result = customerService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Customer with phone - 9999999999 not found",
                result.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteCustomer() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");
        customer.setDeleted(false);

        customerRepository.save(customer);

        boolean result =
                customerService.delete("9876543210");

        assertTrue(result);

        Customer deleted =
                customerRepository.findByPhoneNo("9876543210");

        assertTrue(deleted.getDeleted());

        verify(addressService)
                .delete("9876543210");
    }

    @Test
    void delete_ShouldReturnFalse_WhenNotFound() {

        boolean result =
                customerService.delete("9999999999");

        assertFalse(result);

        verify(addressService, never())
                .delete(anyString());
    }

    @Test
    void findByIdentifierWithAddressDto_ShouldReturnCustomerWithAddresses() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");

        customerRepository.save(customer);

        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");

        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");

        when(addressService.findAllByPhoneNo("9876543210"))
                .thenReturn(List.of(billing, shipping));

        CustomerDto result =
                customerService.findByIdentifierWithAddressDto("9876543210");

        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
    }

    @Test
    void toggleStatus_ShouldToggleCustomerStatus() {

        Customer customer = new Customer();
        customer.setPhoneNo("9876543210");
        customer.setStatus(true);

        customerRepository.save(customer);

        CustomerDto result =
                customerService.toggleStatus("9876543210");

        assertFalse(result.isStatus());
    }

    @Test
    void findIfTrue_ShouldReturnOnlyActiveCustomers() {

        Customer active = new Customer();
        active.setPhoneNo("1111111111");
        active.setStatus(true);
        active.setDeleted(false);

        Customer inactive = new Customer();
        inactive.setPhoneNo("2222222222");
        inactive.setStatus(false);
        inactive.setDeleted(false);

        customerRepository.save(active);
        customerRepository.save(inactive);

        List<CustomerDto> result =
                customerService.findIfTrue();

        assertEquals(1, result.size());
        assertEquals("1111111111",
                result.get(0).getPhoneNo());
    }

    @Test
    void findAll_ShouldReturnPagedCustomers() {

        Customer c1 = new Customer();
        c1.setPhoneNo("1111111111");
        c1.setDeleted(false);

        Customer c2 = new Customer();
        c2.setPhoneNo("2222222222");
        c2.setDeleted(false);

        customerRepository.save(c1);
        customerRepository.save(c2);

        WsDto<CustomerDto> result =
                customerService.findAll(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }
}
