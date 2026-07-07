package com.ust.pos;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.impl.CustomerServiceImpl;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        dto = new CustomerDto();
        dto.setIdentifier("C001");
        dto.setPhoneNo(NUMBER);
        dto.setBillingAddress(new AddressDto());
        dto.setShippingAddress(new AddressDto());
    }

    @Test
    void findMethodsTest() {

        when(repository.findByIdAndDeletedFalse("C001"))
                .thenReturn(customer);

        when(mapper.map(customer, CustomerDto.class))
                .thenReturn(dto);

        assertNotNull(service.findById("C001"));

        when(repository.findByIdAndDeletedFalse("INVALID"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById("INVALID")
        );

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(customer);

        when(addressService.findAllByPhoneNo(NUMBER))
                .thenReturn(
                        List.of(
                                new AddressDto(),
                                new AddressDto()
                        )
                );

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
    void saveTest() {

        when(repository.findByPhoneNo(NUMBER))
                .thenReturn(null);

        Customer mappedCustomer = new Customer();
        mappedCustomer.setStatus(null);

        when(mapper.map(dto, Customer.class))
                .thenReturn(mappedCustomer);

        CustomerDto result = service.save(dto);

        assertNotNull(result);
        assertTrue(mappedCustomer.getStatus());

        verify(repository).save(mappedCustomer);

        verify(addressService, times(2))
                .save(any(AddressDto.class));

        Customer duplicate = new Customer();
        duplicate.setDeleted(false);

        when(repository.findByPhoneNo(NUMBER))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Customer already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByPhoneNo(NUMBER))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Customer with phoneNo 9876543210 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateAndDeleteTest() {

        customer.setCreatedBy("admin");
        customer.setCreatedOn(LocalDateTime.now());

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(customer);

        doNothing().when(mapper)
                .map(dto, customer);

        CustomerDto result = service.update(dto);

        assertNotNull(result);

        verify(repository).save(customer);

        verify(addressService, times(2))
                .update(any(AddressDto.class));

        CustomerDto invalid = new CustomerDto();
        invalid.setPhoneNo("999");

        when(repository.findByPhoneNoAndDeletedFalse("999"))
                .thenReturn(null);

        result = service.update(invalid);

        assertFalse(result.isSuccess());

        when(repository.findByPhoneNoAndDeletedFalse(NUMBER))
                .thenReturn(customer)
                .thenReturn(null);

        assertTrue(service.delete(NUMBER));

        assertFalse(service.delete(NUMBER));

        verify(addressService).delete(NUMBER);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> page =
                new PageImpl<>(
                        List.of(customer),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<CustomerDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());

        Specification<Customer> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<CustomerDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());

        verify(repository)
                .findAll(any(Specification.class), eq(pageable));
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

        assertNull(
                service.toggleStatus("X")
        );
    }

    @Test
    void toggleStatusFalseToTrueCoverageTest() {

        Customer inactiveCustomer = new Customer();
        inactiveCustomer.setIdentifier("C001");
        inactiveCustomer.setStatus(false);

        CustomerDto customerDto = new CustomerDto();

        when(repository.findByIdAndDeletedFalse("C001"))
                .thenReturn(inactiveCustomer);

        when(mapper.map(inactiveCustomer, CustomerDto.class))
                .thenReturn(customerDto);

        CustomerDto result = service.toggleStatus("C001");

        assertNotNull(result);
        assertTrue(inactiveCustomer.getStatus());

        verify(repository).save(inactiveCustomer);
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

        when(repository.findByStatusIsTrueAndDeletedFalse())
                .thenReturn(Collections.emptyList());

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        assertTrue(
                service.findIfTrue().isEmpty()
        );
    }
}