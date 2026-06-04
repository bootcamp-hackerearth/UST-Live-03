package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.modell.Customer;
import com.ust.pos.modell.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private CustomerServiceImpl service;

    private Customer customer;
    private CustomerDto dto;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setIdentifier("C001");
        customer.setPhoneNo("9876543210");
        customer.setStatus(true);
        AddressDto billing = new AddressDto();
        billing.setAddressType("Billing");
        AddressDto shipping = new AddressDto();
        shipping.setAddressType("Shipping");
        dto = new CustomerDto();
        dto.setIdentifier("C001");
        dto.setPhoneNo("9876543210");
        dto.setBillingAddress(billing);
        dto.setShippingAddress(shipping);
    }

    @Test
    void findById_shouldHandleBothCases() {
        when(repository.findById("C001")).thenReturn(customer);
        when(mapper.map(customer, CustomerDto.class)).thenReturn(dto);
        assertNotNull(service.findById("C001"));
        when(repository.findById("X")).thenReturn(null);
        when(mapper.map(null, CustomerDto.class)).thenReturn(null);
        assertNull(service.findById("X"));
    }

    @Test
    void findWithAddress_shouldSetAddresses() {
        when(repository.findFirstByPhoneNo("9876543210"))
                .thenReturn(Optional.of(customer));
        when(mapper.map(customer, CustomerDto.class)).thenReturn(dto);
        when(addressService.findAllByPhoneNo("9876543210"))
                .thenReturn(List.of(new AddressDto(), new AddressDto()));
        CustomerDto result =
                service.findByIdentifierWithAddressDto("9876543210");
        assertNotNull(result);
    }

    @Test
    void save_shouldHandleAllCases() {
        when(repository.findById("C001")).thenReturn(null);
        when(mapper.map(dto, Customer.class)).thenReturn(customer);
        when(mapper.map(any(AddressDto.class), eq(AddressDto.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        CustomerDto result = service.save(dto);
        verify(repository).save(customer);
        verify(addressService, times(2)).save(any());
        assertEquals("C001", result.getIdentifier());
        verify(addressService).save(argThat(a ->
                "C001_Billing".equals(a.getIdentifier())));
        verify(addressService).save(argThat(a ->
                "C001_Shipping".equals(a.getIdentifier())));
        when(repository.findById("C001")).thenReturn(customer);
        CustomerDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleBothCases() {
        when(repository.findFirstByPhoneNo("9876543210"))
                .thenReturn(Optional.of(customer));
        when(addressService.findAllByPhoneNo("9876543210"))
                .thenReturn(List.of(new AddressDto(), new AddressDto()));
        CustomerDto result = service.update(dto);
        verify(mapper).map(dto, customer);
        verify(repository).save(customer);
        verify(addressService, times(2)).update(any());
        assertEquals("9876543210", result.getPhoneNo());
        when(repository.findFirstByPhoneNo("000"))
                .thenReturn(Optional.empty());
        dto.setPhoneNo("000");
        CustomerDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
    }

    @Test
    void delete_shouldAlwaysDelete() {
        when(addressService.delete(anyString())).thenReturn(true);
        boolean result = service.delete("9876543210");
        verify(repository).deleteByPhoneNo("9876543210");
        verify(addressService).delete("9876543210");
        assertTrue(result);
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 5);
        Type type = new TypeToken<List<CustomerDto>>() {}.getType();
        Page<Customer> page =
                new PageImpl<>(List.of(customer), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<Customer> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(emptyPage);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }

    @Test
    void toggleAndFindIfTrue_shouldWork() {
        when(repository.findById("C001")).thenReturn(customer);
        when(mapper.map(customer, CustomerDto.class)).thenReturn(dto);
        CustomerDto toggled = service.toggleStatus("C001");
        assertFalse(customer.isStatus());
        verify(repository).save(customer);
        assertNotNull(toggled);
        when(repository.findByStatusIsTrue()).thenReturn(List.of(customer));
        when(mapper.map(any(), any(Type.class))).thenReturn(List.of(dto));
        assertEquals(1, service.findIfTrue().size());
    }
}