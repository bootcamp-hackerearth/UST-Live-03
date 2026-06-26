package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import com.ust.pos.customer.service.CustomerService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String BILLING = "billing";
    public static final String SHIPPING = "shipping";

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
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with identifier '" + identifier + "' not found");
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(addressService.findByPhoneAndAddressType(identifier, BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneAndAddressType(identifier, SHIPPING)
        );
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getPhoneNo();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (existingCustomer.isDeleted()) {
                customerDto.setMessage("Customer with identifier" + identifier + "has been soft deleted.(Rollback by changing status)");
                customerDto.setSuccess(false);
                return customerDto;
            }
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
        setCreatedDetails(customer);
        customerRepository.save(customer);

        return customerDto;
    }

    @Transactional
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

        billingAddress.setPhoneNo(customerDto.getIdentifier());
        billingAddress.setAddressType(BILLING);
        shippingAddress.setPhoneNo(customerDto.getIdentifier());
        shippingAddress.setAddressType(SHIPPING);

        addressService.update(billingAddress);
        addressService.update(shippingAddress);

        modelMapper.map(customerDto, existingCustomer);
        customerDto.setBillingAddress(addressService.findByPhoneAndAddressType(customerDto.getIdentifier(), BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneAndAddressType(customerDto.getIdentifier(), SHIPPING));
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            softDelete(customer);
            setModifiedDetails(customer);
            customerRepository.save(customer);
            addressService.delete(identifier);
        }
    }

    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);

        WsDto<CustomerDto> customerWsDto = new WsDto<>();
        customerWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerWsDto.setTotalRecords(customerPage.getTotalElements());
        customerWsDto.setTotalPages(customerPage.getTotalPages());
        customerWsDto.setSizePerPage(pageable.getPageSize());
        customerWsDto.setPage(pageable.getPageNumber());

        return customerWsDto;
    }

    @Override
    @Transactional
    public CustomerDto toggleStatus(String identifier, boolean status) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            customer.setStatus(status);
            setModifiedDetails(customer);
            customerRepository.save(customer);
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> searchCustomer(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Customer> customers = customerRepository.searchActiveCustomers(query);
        return customers.stream().map(c -> modelMapper.map(c, CustomerDto.class)).toList();
    }
}