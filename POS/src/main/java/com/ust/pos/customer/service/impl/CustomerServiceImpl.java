package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressService addressService;

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        Customer existingcustomer = customerRepository.findByIdentifier(identifier);
        if (existingcustomer != null) {
            customerDto.setMessage("Customer already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        billing.setIdentifier(customerDto.getIdentifier());
        shipping.setIdentifier(customerDto.getIdentifier());
        addressService.save(shipping, billing);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingcustomer = customerRepository.findByIdentifier(identifier);
        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        if (existingcustomer == null) {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        billing.setIdentifier(customerDto.getIdentifier());
        shipping.setIdentifier(customerDto.getIdentifier());
        addressService.update(shipping, billing);
        return customerDto;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBilling(addressService.findByIdentifierAndBilling(identifier));
        customerDto.setShipping(addressService.findByIdentifierAndShipping(identifier));
        return customerDto;
    }

    @Override
    public List<CustomerDto> findAll() {
        Type listtype = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findAll(), listtype);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        addressService.delete(identifier);
        customerRepository.deleteByIdentifier(identifier);
    }
}
