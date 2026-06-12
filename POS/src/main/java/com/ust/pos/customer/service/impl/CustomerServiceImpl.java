package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
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

    public static final String SHIPPING = "shipping";
    public static final String BILLING = "billing";

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
        customerDto.setPhoneNo(identifier);
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
        String identifier = customerDto.getIdentifier();
        customerDto.setPhoneNo(identifier);
        Customer existingCustomer =
                customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage(
                    "Customer with identifier - " + identifier + " not found"
            );
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billing = customerDto.getBillingAddress();
        AddressDto shipping = customerDto.getShippingAddress();
        billing.setPhoneNo(identifier);
        billing.setAddressType(BILLING);
        shipping.setPhoneNo(identifier);
        shipping.setAddressType(SHIPPING);
        addressService.update(billing);
        addressService.update(shipping);
        modelMapper.map(customerDto, existingCustomer);
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
    public CustomerDto toggleStatus(String identifier, boolean status) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            CustomerDto dto = new CustomerDto();
            dto.setSuccess(false);
            dto.setMessage("Customer not found");
            return dto;
        }
        customer.setStatus(!customer.isStatus());
        customerRepository.save(customer);
        CustomerDto dto = modelMapper.map(customer, CustomerDto.class);
        dto.setSuccess(true);
        dto.setMessage("Status updated successfully");
        return dto;
    }

    @Override
    public PaginationResponseDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        PaginationResponseDto<CustomerDto> response = new PaginationResponseDto<>();
        if (pageable == null) {
            List<Customer> customers = customerRepository.findAll();
            response.setDtoList(modelMapper.map(customers, listType));
            response.setTotalRecords((long) customers.size());
            response.setTotalPages(1);
            response.setSizePerPage(customers.size());
            response.setPage(0);
        } else {
            Page<Customer> customerPage = customerRepository.findAll(pageable);
            response.setDtoList(modelMapper.map(customerPage.getContent(), listType));
            response.setTotalRecords(customerPage.getTotalElements());
            response.setTotalPages(customerPage.getTotalPages());
            response.setSizePerPage(pageable.getPageSize());
            response.setPage(pageable.getPageNumber());
        }
        return response;
    }
}