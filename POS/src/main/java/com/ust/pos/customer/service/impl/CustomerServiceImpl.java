package com.ust.pos.customer.service.impl;

import com.ust.pos.address.AddressService;
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
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return null;
        }

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(
                addressService.findByPhoneAndAddressType(identifier, BILLING));
        customerDto.setShippingAddress(
                addressService.findByPhoneAndAddressType(identifier, SHIPPING));

        return customerDto;
    }


    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String phoneNo = customerDto.getPhoneNo();

        Customer existingCustomer = customerRepository.findByIdentifier(phoneNo);
        if (existingCustomer != null) {
            customerDto.setMessage(
                    "Customer with phone number - " + phoneNo + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        if (billingAddress == null) {
            billingAddress = new AddressDto();
            customerDto.setBillingAddress(billingAddress);
        }

        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (shippingAddress == null) {
            shippingAddress = new AddressDto();
            customerDto.setShippingAddress(shippingAddress);
        }

        billingAddress.setPhoneNo(phoneNo);
        billingAddress.setAddressType(BILLING);

        shippingAddress.setPhoneNo(phoneNo);
        shippingAddress.setAddressType(SHIPPING);

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        customer.setIdentifier(phoneNo);

        customerRepository.save(customer);

        customerDto.setSuccess(true);
        return customerDto;
    }

    @Transactional
    @Override
    public CustomerDto update(CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();

        Customer customer =
                customerRepository.findByIdentifier(identifier);

        if (customer == null) {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billing =
                addressService.findByPhoneAndAddressType(identifier, BILLING);
        AddressDto shipping =
                addressService.findByPhoneAndAddressType(identifier, SHIPPING);

        if (billing == null) {
            billing = new AddressDto();
            billing.setPhoneNo(identifier);
            billing.setAddressType(BILLING);
        }
        if (shipping == null) {
            shipping = new AddressDto();
            shipping.setPhoneNo(identifier);
            shipping.setAddressType(SHIPPING);
        }
        if (customerDto.getBillingAddress() != null) {
            modelMapper.map(customerDto.getBillingAddress(), billing);
        }
        if (customerDto.getShippingAddress() != null) {
            modelMapper.map(customerDto.getShippingAddress(), shipping);
        }

        addressService.update(billing);
        addressService.update(shipping);

        modelMapper.map(customerDto, customer);
        customerRepository.save(customer);

        customerDto.setSuccess(true);
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
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }
}