package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.ShelfsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
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

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        return modelMapper.map(customerRepository.findByIdentifier(identifier), CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        List<AddressDto> addressDtoList = addressService.findAllByPhoneNo(identifier);
        customerDto.setBillingAddress(addressDtoList.get(0));
        customerDto.setShippingAddress(addressDtoList.get(1));
        return customerDto;
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
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customerRepository.save(customer);
        AddressDto billingAddress = modelMapper.map(customerDto.getBillingAddress(), AddressDto.class);
        billingAddress.setIdentifier(customerDto.getIdentifier() + "_" + "Billing");
        billingAddress.setAddressType("Billing");
        billingAddress.setPhoneNo(customerDto.getIdentifier());

        addressService.save(billingAddress);
        AddressDto shippingAddress = modelMapper.map(customerDto.getShippingAddress(), AddressDto.class);
        shippingAddress.setIdentifier(customerDto.getIdentifier() + "_" + "Shipping");
        shippingAddress.setAddressType("Shipping");
        shippingAddress.setPhoneNo(customerDto.getIdentifier());
        addressService.save(shippingAddress);
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
        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);
        List<AddressDto> addresses = addressService.findAllByPhoneNo(customerDto.getIdentifier());
        AddressDto billingAddress = customerDto.getBillingAddress();
        billingAddress.setIdentifier(addresses.get(0).getIdentifier());
        addressService.update(billingAddress);
        AddressDto shippingAddress = customerDto.getShippingAddress();
        shippingAddress.setIdentifier(addresses.get(1).getIdentifier());
        addressService.update(shippingAddress);
        return customerDto;
    }

    @Override
    public boolean delete(String identifier) {
        customerRepository.deleteByIdentifier(identifier);
        addressService.delete(identifier);
        return true;
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findIfTrue() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findByStatusIsTrue(), listType);
    }
}