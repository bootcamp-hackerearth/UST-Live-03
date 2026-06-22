package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.common.CommonService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl extends CommonService implements CustomerService {
    public static final String SHIPPING = "Shipping";
    public static final String BILLING = "Billing";
    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        return modelMapper.map(customerRepository.findByIdentifier(identifier), CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + identifier);
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        List<AddressDto> addressDtoList = addressService.findAllByPhoneNo(identifier);
        AddressDto billing = null;
        AddressDto shipping = null;
        for (AddressDto address : addressDtoList) {
            if (BILLING.equalsIgnoreCase(address.getAddressType())) {
                billing = address;
            } else if (SHIPPING.equalsIgnoreCase(address.getAddressType())) {
                shipping = address;
            }
        }
        customerDto.setBillingAddress(billing != null ? billing : new AddressDto());
        customerDto.setShippingAddress(shipping != null ? shipping : new AddressDto());
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (existingCustomer.isDeleted()) {
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " has been soft deleted.(Rollback by changing status");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);
        customerRepository.save(customer);

        if (customerDto.getBillingAddress() != null) {
            AddressDto billingAddress = modelMapper.map(customerDto.getBillingAddress(), AddressDto.class);
            billingAddress.setIdentifier(customerDto.getIdentifier() + "_" + BILLING);
            billingAddress.setPhoneNo(customerDto.getIdentifier());
            billingAddress.setAddressType(BILLING);
            addressService.save(billingAddress);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingAddress = modelMapper.map(customerDto.getShippingAddress(), AddressDto.class);
            shippingAddress.setIdentifier(customerDto.getIdentifier() + "_" + SHIPPING);
            shippingAddress.setPhoneNo(customerDto.getIdentifier());
            shippingAddress.setAddressType(SHIPPING);
            addressService.save(shippingAddress);
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer saved successfully");
        return customerDto;
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
        setAuditFields(existingCustomer, false);
        customerRepository.save(existingCustomer);

        List<AddressDto> existingAddresses = addressService.findAllByPhoneNo(identifier);
        for (AddressDto existing : existingAddresses) {
            if (BILLING.equalsIgnoreCase(existing.getAddressType()) && customerDto.getBillingAddress() != null) {
                AddressDto billing = customerDto.getBillingAddress();
                billing.setIdentifier(existing.getIdentifier());
                addressService.update(billing);
            }
            if (SHIPPING.equalsIgnoreCase(existing.getAddressType()) && customerDto.getShippingAddress() != null) {
                AddressDto shipping = customerDto.getShippingAddress();
                shipping.setIdentifier(existing.getIdentifier());
                addressService.update(shipping);
            }
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    public boolean delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return false;
        }
        softDelete(customer);
        setAuditFields(customer, false);
        customerRepository.save(customer);
        addressService.delete(identifier);
        return true;
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        Customer updated = customerRepository.save(customer);
        return modelMapper.map(updated, CustomerDto.class);
    }
}