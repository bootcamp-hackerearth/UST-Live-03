package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
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
    void findByIdTest() {
        when(repository.findById("C001")).thenReturn(customer);
        when(mapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto result = service.findById("C001");
        assertNotNull(result);

        when(repository.findById("X")).thenReturn(null);
        when(mapper.map(null, CustomerDto.class)).thenReturn(null);

        assertNull(service.findById("X"));
    }

    @Test
    void findWithAddressTest() {
        when(repository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(mapper.map(customer, CustomerDto.class)).thenReturn(dto);
        when(addressService.findAllByPhoneNo("9876543210"))
                .thenReturn(List.of(new AddressDto(), new AddressDto()));

        CustomerDto result = service.findByIdentifierWithAddressDto("9876543210");
        assertNotNull(result);
    }

    @Test
    void saveTest() {
        when(repository.findById("C001")).thenReturn(null);
        when(mapper.map(dto, Customer.class)).thenReturn(customer);
        when(mapper.map(any(AddressDto.class), eq(AddressDto.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CustomerDto result = service.save(dto);
        verify(repository).save(customer);
        verify(addressService, times(2)).save(any());
        assertEquals("C001", result.getIdentifier());

        verify(addressService).save(argThat(a -> "C001_Billing".equals(a.getIdentifier())));
        verify(addressService).save(argThat(a -> "C001_Shipping".equals(a.getIdentifier())));

        when(repository.findById("C001")).thenReturn(customer);
        CustomerDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
        when(repository.findByPhoneNo("9876543210")).thenReturn(customer);
        when(addressService.findAllByPhoneNo("9876543210"))
                .thenReturn(List.of(new AddressDto(), new AddressDto()));

        CustomerDto result = service.update(dto);
        verify(mapper).map(dto, customer);
        verify(repository).save(customer);
        verify(addressService, times(2)).update(any());
        assertEquals("9876543210", result.getPhoneNo());

        when(repository.findByPhoneNo("000")).thenReturn(null);
        dto.setPhoneNo("000");

        CustomerDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
    }

    @Test
    void deleteTest() {
        when(addressService.delete(anyString())).thenReturn(true);

        boolean result = service.delete("9876543210");

        verify(repository).deleteByPhoneNo("9876543210");
        verify(addressService).delete("9876543210");
        assertTrue(result);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Type type = new TypeToken<List<CustomerDto>>(){}.getType();

        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<CustomerDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Customer> empty = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(empty.getContent(), type)).thenReturn(List.of());

        WsDto<CustomerDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }

    @Test
    void toggleAndFindIfTrueTest() {
        when(repository.findById("C001")).thenReturn(customer);
        when(mapper.map(customer, CustomerDto.class)).thenReturn(dto);

        CustomerDto toggled = service.toggleStatus("C001");
        assertFalse(customer.getStatus());
        verify(repository).save(customer);
        assertNotNull(toggled);

        Type type = new TypeToken<List<CustomerDto>>(){}.getType();
        when(repository.findByStatusIsTrue()).thenReturn(List.of(customer));
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));

        List<CustomerDto> result = service.findIfTrue();
        assertEquals(1, result.size());
    }
}