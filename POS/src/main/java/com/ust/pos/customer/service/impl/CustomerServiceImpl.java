package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String BILLING = "billing";
    public static final String SHIPPING = "shipping";
    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(
            CustomerRepository customerRepository,
            ModelMapper modelMapper,
            AddressService addressService
    ) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null || customer.isDeleted()) {
            return null;
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(addressService.findByPhoneAndAddressType(identifier, BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneAndAddressType(identifier, SHIPPING));
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getPhoneNo();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (existingCustomer.isDeleted()) {
                customerDto.setSuccess(false);
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " is already soft deleted");
                return customerDto;
            }
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
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
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer created successfully");
        return customerDto;
    }

    @Override
    @Transactional
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null || existingCustomer.isDeleted()) {
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " not found");
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
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        customerDto.setBillingAddress(addressService.findByPhoneAndAddressType(identifier, BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneAndAddressType(identifier, SHIPPING));
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null && !customer.isDeleted()) {
            softDelete(customer);
            setModifiedDetails(customer);
            customerRepository.save(customer);
            addressService.delete(identifier);
        }
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        wsDto.setPage(customerPage.getNumber());
        wsDto.setSizePerPage(customerPage.getSize());
        wsDto.setTotalPages(customerPage.getTotalPages());
        wsDto.setTotalRecords(customerPage.getTotalElements());
        return wsDto;
    }

    @Override
    @Transactional
    public CustomerDto toggleStatus(String identifier, boolean status) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null || customer.isDeleted()) {
            CustomerDto response = new CustomerDto();
            response.setSuccess(false);
            response.setMessage("Customer not found");
            return response;
        }
        customer.setStatus(status);
        setModifiedDetails(customer);
        customerRepository.save(customer);
        CustomerDto response = modelMapper.map(customer, CustomerDto.class);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");
        return response;
    }

    @Override
    public List<CustomerDto> searchCustomer(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Customer> customers = customerRepository.searchActiveCustomers(query);
        return customers.stream().map(c -> modelMapper.map(c, CustomerDto.class)).toList();
    }

    @Override
    public WsDto<CustomerDto> findAll(Specification<Customer> specification,
                                      Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Page<Customer> page =
                customerRepository.findAll(specification, pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();

        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}