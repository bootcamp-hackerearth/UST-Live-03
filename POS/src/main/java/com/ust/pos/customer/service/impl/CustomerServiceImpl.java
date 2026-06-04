package com.ust.pos.customer.service.impl;

import com.ust.pos.adress.service.AddressService;
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

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";
    private static final String BILLING = "billing";
    private static final String SHIPPING = "shipping";
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingAddress = customerDto.getBillingAddress();
            String billIdentifier = customerDto.getUsername() + "_billing_" + customerDto.getIdentifier();
            billingAddress.setIdentifier(billIdentifier);
            billingAddress.setPhoneNo(customerDto.getIdentifier());
            billingAddress.setCustomerName(customer.getCustomerName());
            billingAddress.setAddressType(BILLING);
            addressService.save(billingAddress);
        }
        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingAddress = customerDto.getShippingAddress();
            String shipIdentifier = customerDto.getUsername() + "_shipping_" + customerDto.getIdentifier();
            shippingAddress.setIdentifier(shipIdentifier);
            shippingAddress.setPhoneNo(customerDto.getIdentifier());
            shippingAddress.setCustomerName(customer.getCustomerName());
            shippingAddress.setAddressType(SHIPPING);
            addressService.save(shippingAddress);
        }
        customerDto.setSuccess(true);
        customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " Added Successfully");
        return customerDto;
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public List<CustomerDto> findAllActive() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findAllByStatus(true), listType);
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);
        List<AddressDto> existingAddresses = addressService.findAllByPhoneNumber(identifier);
        AddressDto existingBilling = null;
        AddressDto existingShipping = null;
        for (AddressDto addr : existingAddresses) {
            if (BILLING.equalsIgnoreCase(addr.getAddressType())) {
                existingBilling = addr;
            } else if (SHIPPING.equalsIgnoreCase(addr.getAddressType())) {
                existingShipping = addr;
            }
        }
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingDto = customerDto.getBillingAddress();
            if (existingBilling != null) {
                billingDto.setIdentifier(existingBilling.getIdentifier());
            }
            billingDto.setPhoneNo(identifier);
            billingDto.setCustomerName(existingCustomer.getCustomerName());
            billingDto.setAddressType(BILLING);
            addressService.update(billingDto);
        }
        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingDto = customerDto.getShippingAddress();
            if (existingShipping != null) {
                shippingDto.setIdentifier(existingShipping.getIdentifier());
            }
            shippingDto.setPhoneNo(identifier);
            shippingDto.setCustomerName(existingCustomer.getCustomerName());
            shippingDto.setAddressType(SHIPPING);
            addressService.update(shippingDto);
        }
        customerDto.setSuccess(true);
        customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " Updated");
        return customerDto;
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public boolean delete(String identifier) {
        customerRepository.deleteByIdentifier(identifier);
        addressService.delete(identifier);
        return true;
    }
}
