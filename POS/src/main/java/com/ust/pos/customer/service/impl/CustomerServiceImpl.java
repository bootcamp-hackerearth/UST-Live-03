package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.model.Customer;
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
    public static final String SHIPPING = "shipping";
    public static final String BILLING = "billing";

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CustomerRepository customerRepository;

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
        Customer existing = customerRepository.findByIdentifier(customerDto.getIdentifier());
        if (existing != null) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer already exists : " + customerDto.getIdentifier());
            return customerDto;
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingAddress = customerDto.getBillingAddress();
            String billIdentifier = customerDto.getUsername() + "_billing_" +customerDto.getIdentifier();
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
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        customerRepository.deleteByIdentifier(identifier);
        addressService.delete(identifier);
    }

    @Override
    public void updateStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        customerRepository.save(customer);
    }

    @Override
    public List<CustomerDto> findAllActive() {
        Type listType = new TypeToken<List<CategoryDto>>() {
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
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer not found : " + customerDto.getIdentifier());
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
            billingDto.setPhoneNo(customerDto.getIdentifier());
            billingDto.setCustomerName(existingCustomer.getCustomerName());
            billingDto.setAddressType(BILLING);
            addressService.update(billingDto);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingDto = customerDto.getShippingAddress();
            if (existingShipping != null) {
                shippingDto.setIdentifier(existingShipping.getIdentifier());
            }
            shippingDto.setPhoneNo(customerDto.getIdentifier());
            shippingDto.setCustomerName(existingCustomer.getCustomerName());
            shippingDto.setAddressType(SHIPPING);
            addressService.update(shippingDto);
        }
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    public CustomerDto changeToggleStatus(String identifier, boolean status) {
        Customer customer=customerRepository.findByIdentifier(identifier);
        if(customer!=null)
        {
            customer.setStatus(status);
            customerRepository.save(customer);
        }
        return modelMapper.map(customer, CustomerDto.class);
    }
}
