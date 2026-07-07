package com.ust.pos;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CustomerServiceImplIT {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @MockitoBean
    private AddressServiceImpl addressService;

    @BeforeEach
    void cleanUp() {
        customerRepository.deleteAll();
    }

    @Test
    void save_shouldCreateCustomerAndAddresses() {
        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST001");
        dto.setStatus(true);

        AddressDto billing = new AddressDto();
        dto.setBillingAddress(billing);

        AddressDto shipping = new AddressDto();
        dto.setShippingAddress(shipping);

        CustomerDto response = customerService.save(dto);

        assertNotNull(response);
        Customer saved = customerRepository.findByIdentifier("CUST001");
        assertNotNull(saved);
        assertEquals("CUST001", saved.getIdentifier());

        Mockito.verify(addressService, Mockito.times(1)).save(Mockito.argThat(addr ->
                "CUST001_Billing".equals(addr.getIdentifier()) && "Billing".equals(addr.getAddressType())
        ));
        Mockito.verify(addressService, Mockito.times(1)).save(Mockito.argThat(addr ->
                "CUST001_Shipping".equals(addr.getIdentifier()) && "Shipping".equals(addr.getAddressType())
        ));
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setDeleted(false);
        customerRepository.save(customer);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST001");

        CustomerDto response = customerService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Customer with identifier - CUST001 already exists", response.getMessage());
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setDeleted(true);
        customerRepository.save(customer);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST001");

        CustomerDto response = customerService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals("Customer with identifier CUST001 was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void update_shouldUpdateCustomerDetailsAndAddresses() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setStatus(true);
        customer.setDeleted(false);
        customerRepository.save(customer);

        CustomerDto dto = new CustomerDto();
        dto.setIdentifier("CUST001");
        dto.setStatus(false);

        AddressDto billing = new AddressDto();
        dto.setBillingAddress(billing);
        AddressDto shipping = new AddressDto();
        dto.setShippingAddress(shipping);

        List<AddressDto> existingAddresses = new ArrayList<>();
        AddressDto addr1 = new AddressDto();
        addr1.setIdentifier("ADDR_B_001");
        AddressDto addr2 = new AddressDto();
        addr2.setIdentifier("ADDR_S_001");
        existingAddresses.add(addr1);
        existingAddresses.add(addr2);

        Mockito.when(addressService.findAllByPhoneNo("CUST001")).thenReturn(existingAddresses);

        CustomerDto response = customerService.update(dto);
        assertTrue(response.isSuccess());

        Customer updated = customerRepository.findByIdentifier("CUST001");
        assertFalse(updated.isStatus());

        Mockito.verify(addressService, Mockito.times(1)).update(Mockito.argThat(addr -> "ADDR_B_001".equals(addr.getIdentifier())));
        Mockito.verify(addressService, Mockito.times(1)).update(Mockito.argThat(addr -> "ADDR_S_001".equals(addr.getIdentifier())));
    }

    @Test
    void findByIdentifier_shouldReturnCustomer() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customerRepository.save(customer);

        CustomerDto result = customerService.findByIdentifier("CUST001");
        assertNotNull(result);
        assertEquals("CUST001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void findByIdentifierWithAddressDto_shouldAssembleCustomerDtoComplete() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customerRepository.save(customer);

        List<AddressDto> addressList = new ArrayList<>();
        AddressDto b = new AddressDto();
        b.setIdentifier("CUST001_Billing");
        AddressDto s = new AddressDto();
        s.setIdentifier("CUST001_Shipping");
        addressList.add(b);
        addressList.add(s);

        Mockito.when(addressService.findAllByPhoneNo("CUST001")).thenReturn(addressList);

        CustomerDto result = customerService.findByIdentifierWithAddressDto("CUST001");
        assertNotNull(result);
        assertEquals("CUST001", result.getIdentifier());
        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());
        assertEquals("CUST001_Billing", result.getBillingAddress().getIdentifier());
        assertEquals("CUST001_Shipping", result.getShippingAddress().getIdentifier());
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setStatus(true);
        customerRepository.save(customer);

        customerService.toggleStatus("CUST001");
        Customer updated = customerRepository.findByIdentifier("CUST001");
        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDeleteAndRemoveAddresses() {
        Customer customer = new Customer();
        customer.setIdentifier("CUST001");
        customer.setDeleted(false);
        customerRepository.save(customer);

        boolean isDeleted = customerService.delete("CUST001");
        assertTrue(isDeleted);

        Customer deleted = customerRepository.findByIdentifier("CUST001");
        assertTrue(deleted.isDeleted());
        Mockito.verify(addressService, Mockito.times(1)).delete("CUST001");
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Customer customer1 = new Customer();
        customer1.setIdentifier("CUST001");
        customer1.setDeleted(false);
        customerRepository.save(customer1);

        Pageable pageable = PageRequest.of(0, 10);
        WsDto<CustomerDto> response = customerService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Customer activeCustomer = new Customer();
        activeCustomer.setIdentifier("CUST001");
        activeCustomer.setStatus(true);
        activeCustomer.setDeleted(false);
        customerRepository.save(activeCustomer);

        List<CustomerDto> activeList = customerService.findIfTrue();
        assertFalse(activeList.isEmpty());
    }
}