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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    public static final String NUMBER = "9876543210";
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
        customer.setPhoneNo(NUMBER);
        customer.setStatus(true);
        customer.setDeleted(false);

        dto = new CustomerDto();
        dto.setIdentifier("C001");
        dto.setPhoneNo(NUMBER);
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());
    }

    @Test
    void findByIdTest() {

        when(repository.findByIdAndDeletedFalse("C001"))
                .thenReturn(customer);

        when(mapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        assertNotNull(service.findById("C001"));
    }

    @Test
    void findByIdentifierWithAddressDtoTest() {

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(customer);

        when(mapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        when(addressService.findAllByPhoneNo(NUMBER))
                .thenReturn(List.of(new AddressDto(), new AddressDto()));

        CustomerDto result =
                service.findByIdentifierWithAddressDto(NUMBER);

        assertNotNull(result.getBillingAddress());
        assertNotNull(result.getShippingAddress());

        when(repository.findByPhoneNoAndDeletedFalse("999"))
                .thenReturn(null);

        assertNull(
                service.findByIdentifierWithAddressDto("999")
        );
    }

    @Test
    void saveSuccessTest() {

        when(repository.findByPhoneNo(NUMBER))
                .thenReturn(null);

        when(mapper.map(dto, Customer.class))
                .thenReturn(customer);

        CustomerDto result = service.save(dto);

        verify(repository).save(customer);
        verify(addressService, times(2))
                .save(any(AddressDto.class));

        assertNotNull(result);
    }

    @Test
    void saveDuplicateAndSoftDeletedTest() {

        when(repository.findByPhoneNo(NUMBER))
                .thenReturn(customer);

        CustomerDto duplicate = service.save(dto);

        assertFalse(duplicate.isSuccess());

        customer.setDeleted(true);

        CustomerDto softDeleted = service.save(dto);

        assertFalse(softDeleted.isSuccess());
    }

    @Test
    void updateSuccessTest() {

        customer.setCreatedBy("admin");
        customer.setCreatedOn(LocalDateTime.now());

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(customer);

        doNothing().when(mapper)
                .map(dto, customer);

        CustomerDto result = service.update(dto);

        verify(mapper).map(dto, customer);

        verify(repository).save(customer);

        verify(addressService, times(2))
                .update(any(AddressDto.class));

        assertNotNull(result);
    }

    @Test
    void updateNotFoundTest() {

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(null);

        CustomerDto result = service.update(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void deleteTest() {

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(customer)
                .thenReturn(null);

        assertTrue(service.delete(NUMBER));

        verify(repository).save(customer);
        verify(addressService).delete(NUMBER);

        assertFalse(service.delete(NUMBER));
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Page<Customer> page =
                new PageImpl<>(List.of(customer), pageable, 1);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<CustomerDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        Page<Customer> emptyPage =
                new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(emptyPage);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        assertTrue(
                service.findAll(pageable)
                        .getDtoList()
                        .isEmpty()
        );
    }

    @Test
    void toggleStatusTest() {

        when(repository.findByIdAndDeletedFalse("C001"))
                .thenReturn(customer);

        when(mapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        CustomerDto result =
                service.toggleStatus("C001");

        assertNotNull(result);
        assertFalse(customer.getStatus());

        when(repository.findByIdAndDeletedFalse("X"))
                .thenReturn(null);

        assertNull(service.toggleStatus("X"));
    }

    @Test
    void findIfTrueTest() {

        when(repository.findByStatusIsTrueAndDeletedFalse())
                .thenReturn(List.of(customer));

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        List<CustomerDto> result =
                service.findIfTrue();

        assertEquals(1, result.size());
    }
}