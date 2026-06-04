package com.ust.pos.customer.service.impl;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.AddressRepository;
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
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return null;
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            customerDto.setMessage("Customer with identifier - " + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();
        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        addressService.save(billingAddress);
        addressService.save(shippingAddress);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        return customerDto;
    }

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

        customerDto.setBillingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "billingAddress"));
        customerDto.setShippingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "shippingAddress"));
        modelMapper.map(customerDto, existingCustomer);
        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        customerRepository.save(existingCustomer);
        addressService.update(billingAddress);
        addressService.update(shippingAddress);
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier, Long phoneNo) {
        customerRepository.deleteByIdentifier(identifier);
        addressService.deleteByPhone(phoneNo);
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }
}



