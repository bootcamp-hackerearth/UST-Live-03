package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
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
            customerDto.setSuccess(false);
            if (Boolean.TRUE.equals(existingCustomer.getDeleted())) {
                customerDto.setMessage(
                        CUSTOMER_WITH_IDENTIFIER + identifier + " was deleted and cannot be created again");
            } else {
                customerDto.setMessage(
                        CUSTOMER_WITH_IDENTIFIER + identifier + " already exists"
                );
            }
            return customerDto;
        }
        if (customerDto.getPartyType() == null || customerDto.getPartyType().isBlank()) {
            customerDto.setPartyType("Customer");
        }
        if (customerDto.getCreditLimit() == null) {
            customerDto.setCreditLimit(0.0);
        }
        if (customerDto.getBalance() == null) {
            customerDto.setBalance(0.0);
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
        customerRepository.save(customer);
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (billingAddress != null) {
            billingAddress.setAddressType("Billing");
            billingAddress.setPhoneNumber(customerDto.getPhoneNumber());
            addressService.save(billingAddress);
        }
        if (shippingAddress != null) {
            shippingAddress.setAddressType("Shipping");
            shippingAddress.setPhoneNumber(customerDto.getPhoneNumber());
            addressService.save(shippingAddress);
        }
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer saved successfully");
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingCustomer == null) {
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " not found");
            return customerDto;
        }

        modelMapper.map(customerDto, existingCustomer);
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        if (billingAddress != null) {
            billingAddress.setAddressType("Billing");
            billingAddress.setPhoneNumber(customerDto.getPhoneNumber());
            addressService.update(billingAddress);
        }

        if (shippingAddress != null) {
            shippingAddress.setAddressType("Shipping");
            shippingAddress.setPhoneNumber(customerDto.getPhoneNumber());
            addressService.update(shippingAddress);
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        softDelete(customer);
        setModifiedDetails(customer);
        customerRepository.save(customer);
        addressService.deleteByPhoneNumber(customer.getPhoneNumber());
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAllByDeletedFalse(pageable);
        WsDto<CustomerDto> customerDto = new WsDto<>();
        customerDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerDto.setTotalRecords(customerPage.getTotalElements());
        customerDto.setTotalPage(customerPage.getTotalPages());
        customerDto.setSizePerPage(pageable.getPageSize());
        customerDto.setPage(pageable.getPageNumber());
        return customerDto;

    }

}



