package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
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
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseService implements CustomerService {

    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Page<Customer> customerPage = customerRepository.findByIsDeletedFalse(pageable);

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

        AddressDto billingAddress = addressService.findByPhoneNoAndAddressType(
                customer.getPhoneNo(), "BILLING");

        AddressDto shippingAddress = addressService.findByPhoneNoAndAddressType(
                customer.getPhoneNo(), "SHIPPING");

        customerDto.setBillingAddress(billingAddress);
        customerDto.setShippingAddress(shippingAddress);

        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);

        if (existingCustomer != null) {
            customerDto.setMessage(
                    existingCustomer.isDeleted()
                            ? "Customer - " + identifier + " already exists but was deleted, Please contact Administrator"
                            : "Customer - " + identifier + " already exists"
            );

            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        if (billingAddress != null) {
            billingAddress.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(billingAddress);
        }

        if (shippingAddress != null) {
            shippingAddress.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(shippingAddress);
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
        customerRepository.save(customer);

        return customerDto;
    }

    @Transactional
    @Override
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        addressService.deleteByPhoneNo(customer.getPhoneNo());
        setModifiedDetails(customer);
        softDelete(customer);
    }

    @Override
    public void toggleStatus(String identifier) {
        Customer customer = customerRepository
                .findByIdentifier(identifier);

        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            customerRepository.save(customer);
        }
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

        if (billingAddress != null) {
            billingAddress.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(billingAddress);
        }

        if (shippingAddress != null) {
            shippingAddress.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(shippingAddress);
        }

        modelMapper.map(customerDto, existingCustomer);
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);

        return customerDto;
    }

    @Override
    public WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> page = customerRepository.findAll(example, pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}