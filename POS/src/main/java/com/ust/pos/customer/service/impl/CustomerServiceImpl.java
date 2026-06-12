package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
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
    public static final String SHIPPING_ADDRESS = "Shipping Address";
    public static final String BILLING_ADDRESS = "Billing Address";
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    AddressService addressService;

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String phoneNum = customerDto.getPhoneNum();
        Customer customer = customerRepository.findByPhoneNum(phoneNum);
        if (customer != null) {
            customerDto.setMessage("Customer with Phone Number" + customerDto.getPhoneNum() + "already exist!");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer currentCustomer = new Customer();
        modelMapper.map(customerDto, currentCustomer);
        String identifier = customerDto.getPhoneNum();
        currentCustomer.setIdentifier(identifier);
        customerRepository.save(currentCustomer);

        Address addressBilling = new Address();
        modelMapper.map(customerDto.getBillingAddress(), addressBilling);
        addressBilling.setIdentifier(customerDto.getPhoneNum());
        addressBilling.setEmail(customerDto.getUsername());
        addressBilling.setAddressType(BILLING_ADDRESS);
        addressRepository.save(addressBilling);

        Address shippingAddress = new Address();
        modelMapper.map(customerDto.getShippingAddress(), shippingAddress);
        shippingAddress.setIdentifier(customerDto.getPhoneNum());
        shippingAddress.setEmail(customerDto.getUsername());
        shippingAddress.setAddressType(SHIPPING_ADDRESS);
        addressRepository.save(shippingAddress);

        return customerDto;

    }


    @Override
    public CustomerDto findByIdentifier(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        //  Fetch BOTH addresses
        AddressDto billing = addressService.findByIdentifierAndAddressType(identifier, BILLING_ADDRESS);
        AddressDto shipping = addressService.findByIdentifierAndAddressType(identifier, SHIPPING_ADDRESS);
        //  Attach them to customer DTO
        customerDto.setBillingAddress(billing);
        customerDto.setShippingAddress(shipping);

        return customerDto;
    }

    @Override
    @Transactional
    public void deleteByIdentifier(String identifier) {
        customerRepository.deleteByIdentifier(identifier);
    }

    @Override
    public CustomerDto changeCustomerStatus(String identifier, boolean status) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            customer.setStatus(status);
            customerRepository.save(customer);
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        WsDto<CustomerDto> customerWsDto = new WsDto<>();
        customerWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerWsDto.setTotalRecords(customerPage.getTotalElements());
        customerWsDto.setTotalPages(customerPage.getTotalPages());
        customerWsDto.setSizePerPage(pageable.getPageSize());
        customerWsDto.setPage(pageable.getPageNumber());

        return customerWsDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage("Customer with Phone Number_" + customerDto.getPhoneNum() + "_not found!");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);
        //Billing
        AddressDto billingAddress = addressService.findByIdentifierAndAddressType(customerDto.getIdentifier(), BILLING_ADDRESS);
        if (billingAddress == null) {
            customerDto.setMessage("Customer" + customerDto.getIdentifier() + "Not found for address type as Billing");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto.getBillingAddress(), billingAddress);
        addressService.update(billingAddress);
        //Shipping
        AddressDto shippingAddress = addressService.findByIdentifierAndAddressType(customerDto.getIdentifier(), SHIPPING_ADDRESS);
        if (shippingAddress == null) {
            customerDto.setMessage("Customer" + customerDto.getIdentifier() + "Not found for address type as Shipping");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto.getShippingAddress(), shippingAddress);
        addressService.update(shippingAddress);
        customerDto.setSuccess(true);
        return customerDto;
    }

    @Override
    public CustomerDto findById(Long id) {

        return customerRepository.findById(id)
                .map(customer -> modelMapper.map(customer, CustomerDto.class))
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}