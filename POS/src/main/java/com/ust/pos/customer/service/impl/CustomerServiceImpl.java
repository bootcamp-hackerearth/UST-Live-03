package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String CUSTOMER_NOT_FOUND = "Customer not found";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ModelMapper modelMapper,
                               AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();

        Page<Customer> page = customerRepository.findByDeletedFalse(pageable);

        WsDto<CustomerDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());

        return ws;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer == null || Boolean.TRUE.equals(customer.getDeleted())) {
            CustomerDto dto = new CustomerDto();
            dto.setSuccess(false);
            dto.setMessage(CUSTOMER_NOT_FOUND);
            return dto;
        }

        CustomerDto dto = modelMapper.map(customer, CustomerDto.class);

        dto.setBillingAddress(
                addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), "billing")
        );

        dto.setShippingAddress(
                addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), "shipping")
        );

        return dto;
    }

    @Override
    public CustomerDto save(CustomerDto dto) {

        if (dto.getPhoneNo() == null || !dto.getPhoneNo().matches("\\d{10}")) {
            dto.setSuccess(false);
            dto.setMessage("Valid 10-digit phone required");
            return dto;
        }

        Customer existing = customerRepository.findByPhoneNo(dto.getPhoneNo());

        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage("Customer already exists");
            return dto;
        }

        saveAddresses(dto);

        Customer customer = modelMapper.map(dto, Customer.class);

        customer.setIdentifier(dto.getPhoneNo());
        customer.setStatus(customer.getStatus() == null || customer.getStatus());

        setCreatedDetails(customer);

        customerRepository.save(customer);

        dto.setSuccess(true);
        dto.setMessage("Customer created successfully");

        return dto;
    }

    @Override
    public CustomerDto update(CustomerDto dto) {

        Customer existing = customerRepository.findByIdentifier(dto.getIdentifier());

        if (existing == null || Boolean.TRUE.equals(existing.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(CUSTOMER_NOT_FOUND);
            return dto;
        }

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setBalance(dto.getBalance());
        existing.setBalanceType(dto.getBalanceType());
        existing.setPartyType(dto.getPartyType());
        existing.setCreditLimit(dto.getCreditLimit());

        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        saveAddresses(dto);

        setModifiedDetails(existing);

        customerRepository.save(existing);

        dto.setSuccess(true);
        dto.setMessage("Customer updated successfully");

        return dto;
    }

    @Override
    public void delete(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer == null) return;

        customer.setDeleted(true);

        setModifiedDetails(customer);

        customerRepository.save(customer);

        addressService.delete(customer.getPhoneNo());
    }

    @Override
    public List<CustomerDto> findActive() {

        List<Customer> list = customerRepository.findByStatusTrueAndDeletedFalse();

        Type type = new TypeToken<List<CustomerDto>>() {}.getType();

        return modelMapper.map(list, type);
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

        CustomerDto dto = new CustomerDto();

        if (customer == null || Boolean.TRUE.equals(customer.getDeleted())) {
            dto.setSuccess(false);
            dto.setMessage(CUSTOMER_NOT_FOUND);
            return dto;
        }

        customer.setStatus(!Boolean.TRUE.equals(customer.getStatus()));

        setModifiedDetails(customer);

        customerRepository.save(customer);

        dto.setIdentifier(customer.getIdentifier());
        dto.setName(customer.getName());
        dto.setPhoneNo(customer.getPhoneNo());
        dto.setStatus(customer.getStatus());

        dto.setSuccess(true);
        dto.setMessage("Status updated");

        return dto;
    }

    @Override
    public List<CustomerDto> searchCustomer(String query) {

        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Customer> list = customerRepository.searchActiveCustomers(query);

        Type type = new TypeToken<List<CustomerDto>>() {}.getType();

        return modelMapper.map(list, type);
    }

    private void saveAddresses(CustomerDto dto) {

        if (dto.getBillingAddress() != null) {
            AddressDto b = dto.getBillingAddress();
            b.setPhoneNo(dto.getPhoneNo());
            b.setAddressType("billing");
            addressService.save(b);
        }

        if (dto.getShippingAddress() != null) {
            AddressDto s = dto.getShippingAddress();
            s.setPhoneNo(dto.getPhoneNo());
            s.setAddressType("shipping");
            addressService.save(s);
        }
    }
}