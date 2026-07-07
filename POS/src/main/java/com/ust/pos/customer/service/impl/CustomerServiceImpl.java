package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.service.BaseService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl extends BaseService implements CustomerService {

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
    public Page<CustomerDto> findAll(Pageable pageable, String search) {
        Page<Customer> customers;
        if (search != null && !search.trim().isEmpty()) {
            Specification<Customer> specification = buildGlobalSearchSpec(Customer.class, search);
            customers = customerRepository.findAll(specification, pageable);
        } else {
            customers = customerRepository.findByDeletedFalse(pageable);
        }
        return customers.map(customer -> modelMapper.map(customer, CustomerDto.class));
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingCustomer != null) {
            customerDto.setMessage("Customer already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        billing.setIdentifier(identifier);
        shipping.setIdentifier(identifier);
        addressService.save(shipping, billing);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();

        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);

        billing.setIdentifier(identifier);
        shipping.setIdentifier(identifier);

        addressService.update(shipping, billing);
        return customerDto;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        CustomerDto customerDto =
                modelMapper.map(customer, CustomerDto.class);
        customerDto.setBilling(addressService.findByIdentifierAndBilling(identifier));
        customerDto.setShipping(addressService.findByIdentifierAndShipping(identifier));
        return customerDto;
    }

    @Override
    public List<CustomerDto> findAll() {
        Type listOfType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(
                customerRepository.findByDeletedFalse(),
                listOfType
        );
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        addressService.delete(identifier);
        Customer customer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (customer != null) {
            customer.setDeleted(true);
            customerRepository.save(customer);
        }
    }
}