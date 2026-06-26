package com.ust.pos.customer.service.impl;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl extends CommonService implements CustomerService {

    public static final String SHIPPING = "shipping";

    public static final String BILLING = "billing";

    private final ModelMapper modelMapper;

    private final CustomerRepository customerRepository;

    private final AddressService addressService;

    public CustomerServiceImpl(ModelMapper modelMapper, CustomerRepository customerRepository, AddressService addressService) {
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
        this.addressService = addressService;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> userPage = customerRepository.findByDeletedFalse(pageable);

        WsDto<CustomerDto> userWsDto = new WsDto<>();
        userWsDto.setDtoList(modelMapper.map(userPage.getContent(), listType));
        userWsDto.setTotalRecords(userPage.getTotalElements());
        userWsDto.setTotalPages(userPage.getTotalPages());
        userWsDto.setSizePerPage(pageable.getPageSize());
        userWsDto.setPage(pageable.getPageNumber());

        return userWsDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        Customer existing = customerRepository.findByIdentifier(customerDto.getIdentifier());
        if (existing != null) {
            if(existing.isDeleted()) {
                customerDto.setMessage("Customer with identifier - " + customerDto.getIdentifier() + "has been soft deleted.(Rollback by changing status");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer already exists : " + customerDto.getIdentifier());
            return customerDto;
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);
        customerRepository.save(customer);
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingAddress = customerDto.getBillingAddress();
            String billIdentifier = customerDto.getUsername() + "_billing_" +customerDto.getIdentifier();
            billingAddress.setIdentifier(billIdentifier);
            billingAddress.setPhoneNo(customerDto.getIdentifier());
            billingAddress.setCustomerName(customer.getCustomerName());
            billingAddress.setAddressType(BILLING);
            addressService.save(billingAddress);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingAddress = customerDto.getShippingAddress();
            String shipIdentifier = customerDto.getUsername() + "_shipping_" + customerDto.getIdentifier();
            shippingAddress.setIdentifier(shipIdentifier);
            shippingAddress.setPhoneNo(customerDto.getIdentifier());
            shippingAddress.setCustomerName(customer.getCustomerName());
            shippingAddress.setAddressType(SHIPPING);
            addressService.save(shippingAddress);
        }
        customerDto.setSuccess(true);
        return customerDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        softDelete(customer);
        setAuditFields(customer,false);
        customerRepository.save(customer);
        return true;
    }

    @Override
    public void updateStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        customerRepository.save(customer);
    }

    @Override
    public List<CustomerDto> findAllActive() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findAllByStatus(true), listType);
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer not found : " + customerDto.getIdentifier());
            return customerDto;
        }

        modelMapper.map(customerDto, existingCustomer);
        setAuditFields(existingCustomer,false);
        customerRepository.save(existingCustomer);
        List<AddressDto> existingAddresses = addressService.findAllByPhoneNumber(identifier);
        AddressDto existingBilling = null;
        AddressDto existingShipping = null;
        for (AddressDto addr : existingAddresses) {
            if (BILLING.equalsIgnoreCase(addr.getAddressType())) {
                existingBilling = addr;
            } else if (SHIPPING.equalsIgnoreCase(addr.getAddressType())) {
                existingShipping = addr;
            }
        }
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingDto = customerDto.getBillingAddress();
            if (existingBilling != null) {
                billingDto.setIdentifier(existingBilling.getIdentifier());
            }
            billingDto.setPhoneNo(customerDto.getIdentifier());
            billingDto.setCustomerName(existingCustomer.getCustomerName());
            billingDto.setAddressType(BILLING);
            addressService.update(billingDto);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingDto = customerDto.getShippingAddress();
            if (existingShipping != null) {
                shippingDto.setIdentifier(existingShipping.getIdentifier());
            }
            shippingDto.setPhoneNo(customerDto.getIdentifier());
            shippingDto.setCustomerName(existingCustomer.getCustomerName());
            shippingDto.setAddressType(SHIPPING);
            addressService.update(shippingDto);
        }
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    public CustomerDto changeToggleStatus(String identifier, boolean status) {
        Customer customer=customerRepository.findByIdentifier(identifier);
        if(customer!=null)
        {
            customer.setStatus(status);
            customerRepository.save(customer);
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findActiveStatus() {
        List<Customer> allCustomers = customerRepository.findAll();
        List<Customer> activeCustomers = allCustomers.stream().filter(Customer::isStatus).toList();

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(activeCustomers, listType);
    }
}
