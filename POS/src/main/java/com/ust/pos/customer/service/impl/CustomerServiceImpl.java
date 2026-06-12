package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.customer.service.CustomerService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    public static final String BILLING = "billing";
    public static final String SHIPPING = "shipping";
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        CustomerDto customerDto = modelMapper.map(customerRepository.findByIdentifier(identifier), CustomerDto.class);
        customerDto.setBillingAddress(addressService.findByPhoneAndAddressType(identifier, BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneAndAddressType(identifier, SHIPPING));
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(customerDto.getPhoneNo());
        if (existingCustomer != null) {
            customerDto.setMessage("Customer with identifier - " + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        billingAddress.setAddressType(BILLING);
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setAddressType(SHIPPING);

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        customer.setIdentifier(customerDto.getPhoneNo());
        customerRepository.save(customer);

        return customerDto;
    }

    @Transactional
    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage("Customer with identifier - " + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(customerDto.getIdentifier());
        billingAddress.setAddressType(BILLING);
        shippingAddress.setPhoneNo(customerDto.getIdentifier());
        shippingAddress.setAddressType(SHIPPING);

        addressService.update(billingAddress);
        addressService.update(shippingAddress);

        modelMapper.map(customerDto, existingCustomer);
        customerDto.setBillingAddress(addressService.findByPhoneAndAddressType(customerDto.getIdentifier(), BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneAndAddressType(customerDto.getIdentifier(), SHIPPING));

        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        customerRepository.deleteByIdentifier(identifier);
        addressService.delete(identifier);
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    @Transactional
    public CustomerDto toggleStatus(String identifier, boolean status) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            customerRepository.save(customer);
        }
        return modelMapper.map(customer, CustomerDto.class);
    }
}