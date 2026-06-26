package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
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
public class CustomerServiceImpl extends CommonService implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper,
                        AddressService addressService) {
        this.customerRepository = customerRepository;
        this.addressService = addressService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String email = customerDto.getEmail();
        Customer existingcustomer = customerRepository.findByEmailAndIsDeleteFalse(email);
        if (existingcustomer != null) {
            customerDto.setMessage(
                    "Customer with email " + email + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        if (billing == null) {
            billing = new AddressDto();
        }
        if (shipping == null) {
            shipping = new AddressDto();
        }
        billing.setIdentifier(customerDto.getEmail());
        shipping.setIdentifier(customerDto.getEmail());
        addressService.save(shipping, billing);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);
        customerRepository.save(customer);
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingcustomer =
                customerRepository.findByIdentifierAndIsDeleteFalse(identifier);
        if (existingcustomer == null) {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto, existingcustomer);
        setAuditFields(existingcustomer, false);
        customerRepository.save(existingcustomer);
        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        if (billing != null && shipping != null) {
            billing.setIdentifier(customerDto.getEmail());
            shipping.setIdentifier(customerDto.getEmail());
            addressService.update(shipping, billing);
        }
        return customerDto;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.
                findByIdentifierAndIsDeleteFalse(identifier);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBilling(addressService.findByIdentifierAndBilling(customer.getEmail()));
        customerDto.setShipping(addressService.findByIdentifierAndShipping(customer.getEmail()));
        return customerDto;
    }

    @Override
    public List<CustomerDto> findAll() {
        Type listtype = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findByIsDeleteFalse(), listtype);
    }

    @Override
    public void delete(String email) {
        Customer customer = customerRepository.findByEmailAndIsDeleteFalse(email);
        if (customer != null) {
            customer.setDelete(true);
            addressService.delete(email);
            setAuditFields(customer, false);
            customerRepository.save(customer);
        }
    }

    @Override
    public List<CustomerDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customePage = customerRepository.findByIsDeleteFalse(pageable);
        return modelMapper.map(customePage.getContent(), listOfType);
    }

    @Override
    public Page<CustomerDto> findAll(Pageable pageable, String search) {
        Page<Customer> customerPage;
        if (search != null && !search.trim().isEmpty()) {
            customerPage = customerRepository.findByIdentifierContainingIgnoreCaseAndIsDeleteFalse
                    (search, pageable);
        } else {
            customerPage = customerRepository.findByIsDeleteFalse(pageable);
        }
        return customerPage.map(customer -> modelMapper.map(customer, CustomerDto.class));
    }
}
