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
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final String SHIPPING = "shipping";
    private static final String BILLING = "billing";

    @Autowired
    private CustomerRepository customerRepository;

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

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(addressService.findByPhoneNoAndAddressType(customerDto.getPhoneNo(), BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneNoAndAddressType(customerDto.getPhoneNo(), SHIPPING));
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String identifier = customerDto.getPhoneNo();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);

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

    @Override
    public CustomerDto update(CustomerDto customerDto) {

        String identifier = customerDto.getPhoneNo();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);

        if (existingCustomer == null) {

            customerDto.setMessage("Customer with identifier - " + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(identifier);
        billingAddress.setAddressType(BILLING);
        shippingAddress.setPhoneNo(identifier);
        shippingAddress.setAddressType(SHIPPING);

        addressService.update(billingAddress);
        addressService.update(shippingAddress);

        modelMapper.map(customerDto, existingCustomer);
        customerDto.setBillingAddress(addressService.findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), SHIPPING));

        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    @Transactional
    public CustomerDto updateStatus(String identifier, boolean status) {
        CustomerDto response = new CustomerDto();

        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            response.setSuccess(false);
            response.setMessage("Product not found");
            return response;
        }

        customer.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
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
        if (pageable == null) {
            return modelMapper.map(customerRepository.findAll(), listType);
        }
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

}
