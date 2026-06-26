package com.ust.pos.customer.service.impl;

import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PaginatedResponseDto;
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
public class CustomerServiceImpl implements CustomerService {

    private static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return null;
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(
                addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), "billingAddress"));
        customerDto.setShippingAddress(
                addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), "shippingAddress"));
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingByIdentifier = customerRepository.findByIdentifier(identifier);

        if (existingByIdentifier != null) {
            if (Boolean.TRUE.equals(existingByIdentifier.getIsDeleted())) {
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " was deleted. Contact admin for further support or try with a different email.");
            } else {
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
            }
            customerDto.setSuccess(false);
            return customerDto;
        }

        Customer existingByPhone = customerRepository.findByPhoneNo(customerDto.getPhoneNo());
        if (existingByPhone != null) {
            if (Boolean.TRUE.equals(existingByPhone.getIsDeleted())) {
                customerDto.setMessage("A deleted customer exists with this phone number. Contact admin for further support or try with a different phone number.");
            } else {
                customerDto.setMessage("Customer with phone number - " + customerDto.getPhoneNo() + " already exists");
            }
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
        customer.setIsDeleted(false);
        customerRepository.save(customer);
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

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        modelMapper.map(customerDto, existingCustomer);
        customerDto.setBillingAddress(addressService.findByPhoneNoAndAddressType(
                existingCustomer.getPhoneNo(), "billingAddress"));
        customerDto.setShippingAddress(addressService.findByPhoneNoAndAddressType(
                existingCustomer.getPhoneNo(), "shippingAddress"));

        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    public CustomerDto delete(String identifier, Long phoneNo) {
        CustomerDto customerDto = new CustomerDto();
        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer == null) {
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        customer.setIsDeleted(true);
        customer.setStatus(false);
        customerRepository.save(customer);
        addressService.softDeleteByPhone(phoneNo);

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer deleted successfully");
        return customerDto;
    }

    @Override
    public PaginatedResponseDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByIsDeleted(false, pageable);
        List<CustomerDto> items = modelMapper.map(customerPage.getContent(), listType);
        PaginatedResponseDto<CustomerDto> response = new PaginatedResponseDto<>();
        response.setItems(items);
        response.setTotalRecords(customerPage.getTotalElements());
        response.setTotalPages(customerPage.getTotalPages());
        response.setSizePerPage(pageable.getPageSize());
        response.setPage(pageable.getPageNumber());
        return response;
    }

    @Override
    public List<CustomerDto> findAllActive() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findByStatusAndIsDeleted(true, false), listType);
    }

    @Override
    public void changeStatus(String identifier, boolean status) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(status);
        customerRepository.save(customer);
    }
}