package com.ust.pos.customer.service.impl;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

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

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier(customerDto.getIdentifier());
        cartService.save(cartDto);

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

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());

        addressService.update(billingAddress);
        addressService.update(shippingAddress);

        customerDto.setBillingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "billingAddress"));
        customerDto.setShippingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "shippingAddress"));

        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);

        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier, Long phoneNo) {

        customerRepository.deleteByIdentifier(identifier);
        addressService.deleteByPhoneNo(phoneNo);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {


        Page<Customer> customerPage = customerRepository.findAll(pageable);

        WsDto<CustomerDto> paginationResponseDto = new WsDto<>();

        List<CustomerDto> customerDtos = customerPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, CustomerDto.class))
                .toList();

        paginationResponseDto.setContent(customerDtos);
        paginationResponseDto.setPage(customerPage.getNumber());
        paginationResponseDto.setSizePerPage(customerPage.getSize());
        paginationResponseDto.setTotalPages(customerPage.getTotalPages());
        paginationResponseDto.setTotalRecords(customerPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            customerRepository.save(customer);
        }
    }

    @Override
    public List<Customer> findActiveCustomer() {

        return customerRepository.findByStatus(true);
    }
}



