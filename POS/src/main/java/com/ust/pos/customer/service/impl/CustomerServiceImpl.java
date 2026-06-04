package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ModelMapper modelMapper;

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
        billingAddress.setPhoneNo(customerDto.getIdentifier());
        billingAddress.setAddressType("billing");
        billingAddress.setIdentifier(customerDto.getIdentifier() + "_" + billingAddress.getAddressType() + "_" + billingAddress.getZipcode());
        addressService.save(billingAddress);

        AddressDto shippingAddress = modelMapper.map(customerDto.getShippingAddress(), AddressDto.class);
        shippingAddress.setPhoneNo(customerDto.getIdentifier());
        shippingAddress.setAddressType("shipping");
        shippingAddress.setIdentifier(customerDto.getIdentifier() + "_" + shippingAddress.getAddressType() + "_" + shippingAddress.getZipcode());
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

        List<AddressDto> addressDtoList = addressService.findByPhoneNo(identifier);

        Address billingAddress = customerDto.getBillingAddress();
        billingAddress.setIdentifier(addressDtoList.get(0).getIdentifier());
        billingAddress.setAddressType(addressDtoList.get(0).getAddressType());
        addressService.update(modelMapper.map(billingAddress, AddressDto.class));

        Address shippingAddress = customerDto.getShippingAddress();
        shippingAddress.setIdentifier(addressDtoList.get(1).getIdentifier());
        shippingAddress.setAddressType(addressDtoList.get(1).getAddressType());
        addressService.update(modelMapper.map(shippingAddress, AddressDto.class));

        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {

        customerRepository.deleteByIdentifier(identifier);
        addressService.delete(identifier);
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        return modelMapper.map(customerPage.getContent(), listType);
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        return modelMapper.map(customerRepository.findByIdentifier(identifier), CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String identifier) {
        CustomerDto customerDto = findByIdentifier(identifier);
        List<AddressDto> addressDtoList = addressService.findByPhoneNo(identifier);
        customerDto.setBillingAddress(modelMapper.map(addressDtoList.get(0), Address.class));
        customerDto.setShippingAddress(modelMapper.map(addressDtoList.get(1), Address.class));
        return customerDto;
    }
}
