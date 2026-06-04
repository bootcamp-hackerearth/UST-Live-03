package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.modell.Customer;
import com.ust.pos.modell.CustomerRepository;
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
    public CustomerDto findById(String identifier) {
        return modelMapper.map(customerRepository.findById(identifier), CustomerDto.class
        );
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String phoneNo) {
        Customer customer = customerRepository.findByPhoneNo(phoneNo);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        List<AddressDto> addressDtoList = addressService.findAllByPhoneNo(phoneNo);

        if (addressDtoList != null) {
            if (addressDtoList.isEmpty()) {
                customerDto.setBillingAddress(addressDtoList.get(0));
            }
            if (addressDtoList.size() > 1) {
                customerDto.setShippingAddress(addressDtoList.get(1));
            }
        }
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findById(identifier);

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
        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        addressService.save(billingAddress);

        AddressDto shippingAddress = modelMapper.map(customerDto.getShippingAddress(), AddressDto.class);
        shippingAddress.setIdentifier(customerDto.getIdentifier() + "_" + "Shipping");
        shippingAddress.setAddressType("Shipping");
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        addressService.save(shippingAddress);
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findByPhoneNo(customerDto.getPhoneNo());

        if (existingCustomer == null) {
            customerDto.setMessage(
                    "Customer with identifier - " + customerDto.getPhoneNo() + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);
        List<AddressDto> addresses = addressService.findAllByPhoneNo(customerDto.getPhoneNo());
        AddressDto billingAddress = addresses.get(0);
        billingAddress.setPhoneNo(existingCustomer.getPhoneNo());
        addressService.update(billingAddress);
        AddressDto shippingAddress = addresses.get(1);
        shippingAddress.setPhoneNo(existingCustomer.getPhoneNo());
        addressService.update(shippingAddress);
        return customerDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        customerRepository.deleteByPhoneNo(phoneNo);
        addressService.delete(phoneNo);
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
        Customer customer = customerRepository.findById(identifier);
        customer.setStatus(!customer.getStatus());
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findIfTrue() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(
                customerRepository.findByStatusIsTrue(),
                listType
        );
    }
}