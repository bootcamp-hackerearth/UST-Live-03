package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> page = customerRepository.findAll(pageable);
        return modelMapper.map(page.getContent(), listType);
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return null;
        }
        CustomerDto dto = modelMapper.map(customer, CustomerDto.class);
        dto.setBillingAddress(addressService.
                findByPhoneNoAndAddressType(customer.getPhoneNo(), "billing"));
        dto.setShippingAddress(addressService.
                findByPhoneNoAndAddressType(customer.getPhoneNo(), "shipping"));
        return dto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        if (customerDto.getPhoneNo() == null || customerDto.getPhoneNo().isEmpty()) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Phone number is required");
            return customerDto;
        }
        if (!customerDto.getPhoneNo().matches("\\d{10}")) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Phone number must be 10 digits");
            return customerDto;
        }
        Customer existing = customerRepository.findByPhoneNo(customerDto.getPhoneNo());
        if (existing != null) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer already exists");
            return customerDto;
        }
        saveAddresses(customerDto);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customer.setIdentifier(customerDto.getPhoneNo());
        if (customer.getStatus() == null) {
            customer.setStatus(true);
        }
        customerRepository.save(customer);
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer created successfully");
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        Customer existing = customerRepository.findByIdentifier(customerDto.getIdentifier());
        if (existing == null) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer not found");
            return customerDto;
        }
        if (!existing.getPhoneNo().equals(customerDto.getPhoneNo())) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Phone number is read-only and cannot be updated");
            return customerDto;
        }
        existing.setName(customerDto.getName());
        existing.setEmail(customerDto.getEmail());
        existing.setBalance(customerDto.getBalance());
        existing.setBalanceType(customerDto.getBalanceType());
        existing.setPartyType(customerDto.getPartyType());
        existing.setCreditLimit(customerDto.getCreditLimit());
        if (customerDto.getStatus() != null) {
            existing.setStatus(customerDto.getStatus());
        }
        saveAddresses(customerDto);
        customerRepository.save(existing);
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            addressService.delete(customer.getPhoneNo());
            customerRepository.delete(customer);
        }
    }

    @Override
    public List<CustomerDto> findActive() {
        List<CustomerDto> result = new ArrayList<>();
        for (Customer c : customerRepository.findAll()) {
            if (Boolean.TRUE.equals(c.getStatus())) {
                result.add(modelMapper.map(c, CustomerDto.class));
            }
        }
        return result;
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        CustomerDto response = new CustomerDto();
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            response.setSuccess(false);
            response.setMessage("Customer not found");
            return response;
        }
        customer.setStatus(!Boolean.TRUE.equals(customer.getStatus()));
        Customer saved = customerRepository.save(customer);
        response.setIdentifier(saved.getIdentifier());
        response.setName(saved.getName());
        response.setPhoneNo(saved.getPhoneNo());
        response.setStatus(saved.getStatus());
        response.setSuccess(true);
        response.setMessage("Customer status updated successfully");
        return response;
    }

    private void saveAddresses(CustomerDto customerDto) {
        if (customerDto.getBillingAddress() != null) {
            AddressDto billing = customerDto.getBillingAddress();
            billing.setPhoneNo(customerDto.getPhoneNo());
            billing.setAddressType("billing");
            addressService.save(billing);
        }
        if (customerDto.getShippingAddress() != null) {
            AddressDto shipping = customerDto.getShippingAddress();
            shipping.setPhoneNo(customerDto.getPhoneNo());
            shipping.setAddressType("shipping");
            addressService.save(shipping);
        }
    }
}