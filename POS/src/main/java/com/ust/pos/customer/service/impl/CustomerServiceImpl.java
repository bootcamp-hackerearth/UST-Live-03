package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseService implements CustomerService {

    private static final String SHIPPING = "shipping";
    private static final String BILLING = "billing";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, AddressService addressService, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.addressService = addressService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {

            return null;
        }

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(addressService.findByPhoneNoAndAddressType(customerDto.getPhoneNo(), BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneNoAndAddressType(customerDto.getPhoneNo(), SHIPPING));
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String identifier = customerDto.getPhoneNo();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);

        if (existingCustomer != null) {

            if (isSoftDeleted(existingCustomer)) {
                customerDto.setMessage(
                        getDeletedMessage("Customer", identifier)
                );
                customerDto.setSuccess(false);
                return customerDto;
            }

            customerDto.setMessage(
                    "Customer with identifier - " + identifier + " already exists"
            );
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

    @Override
    public CustomerDto update(CustomerDto customerDto) {

        String identifier = customerDto.getPhoneNo();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);

        if (existingCustomer == null) {

            customerDto.setMessage(
                    "Customer with identifier - " + identifier + " not found"
            );
            customerDto.setSuccess(false);
            return customerDto;
        }

        if (isSoftDeleted(existingCustomer)) {
            customerDto.setMessage(
                    getDeletedMessage("Customer", identifier)
            );
            customerDto.setSuccess(false);
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
        customerDto.setBillingAddress(addressService.findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), BILLING));
        customerDto.setShippingAddress(addressService.findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), SHIPPING));

        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    @Transactional
    public CustomerDto updateStatus(String identifier, boolean status) {
        CustomerDto response = new CustomerDto();

        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            response.setSuccess(false);
            response.setMessage("Product not found");
            return response;
        }

        setModifiedDetails(customer);
        customer.setStatus(status);
        response.setSuccess(true);
        response.setMessage("Status updated successfully");

        return response;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        softDelete(customer);
        setModifiedDetails(customer);
        customerRepository.save(customer);
        addressService.delete(identifier);
    }

    @Override
    public PaginationResponseDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage =
                customerRepository.findByIsDeletedFalse(pageable);

        List<CustomerDto> customerDtoList =
                modelMapper.map(
                        customerPage.getContent(),
                        listType
                );

        PaginationResponseDto<CustomerDto> paginationResponseDto =
                new PaginationResponseDto<>();

        paginationResponseDto.setDtoList(customerDtoList);
        paginationResponseDto.setPage(customerPage.getNumber());
        paginationResponseDto.setSizePerPage(customerPage.getSize());
        paginationResponseDto.setTotalPages(customerPage.getTotalPages());
        paginationResponseDto.setTotalRecords(
                customerPage.getTotalElements()
        );

        return paginationResponseDto;
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
