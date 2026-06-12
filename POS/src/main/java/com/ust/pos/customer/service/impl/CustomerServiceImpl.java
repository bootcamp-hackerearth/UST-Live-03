package com.ust.pos.customer.service.impl;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CategoryDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Category;
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
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressService addressService;

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Page<Customer> customerPage = customerRepository.findAll(pageable);

        WsDto<CustomerDto> dto = new WsDto<>();

        dto.setContent(modelMapper.map(customerPage.getContent(), listType));
        dto.setTotalRecords(customerPage.getTotalElements());
        dto.setTotalPages(customerPage.getTotalPages());
        dto.setSizePerPage(pageable.getPageSize());
        dto.setPage(pageable.getPageNumber());

        return dto;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);

        AddressDto billingAddress = addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), "BILLING");

        AddressDto shippingAddress = addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), "SHIPPING");

        customerDto.setBillingAddress(billingAddress);
        customerDto.setShippingAddress(shippingAddress);

        return customerDto;
    }


    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            customerDto.setMessage("Customer - " + identifier + " already exists");
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

    @Transactional
    @Override
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        addressService.deleteByPhoneNo(customer.getPhoneNo());
        customerRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage("Customer with customer - " + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);
        return customerDto;
    }
}
