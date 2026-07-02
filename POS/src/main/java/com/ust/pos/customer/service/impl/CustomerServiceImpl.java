package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.api.BaseService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl extends BaseService implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper,
                               AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();

        Customer existingcustomer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingcustomer != null) {
            customerDto.setMessage("Customer already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();

        if (billing == null) {
            billing = new AddressDto();
            customerDto.setBilling(billing);
        }

        if (shipping == null) {
            shipping = new AddressDto();
            customerDto.setShipping(shipping);
        }

        billing.setIdentifier(identifier);
        shipping.setIdentifier(identifier);

        addressService.save(shipping, billing);

        Customer customer =
                modelMapper.map(customerDto, Customer.class);

        customerRepository.save(customer);

        customerDto.setSuccess(true);

        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();

        Customer existingCustomer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existingCustomer == null) {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);

        // Preserve existing ID
        customer.setId(existingCustomer.getId());

        customerRepository.save(customer);

        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();

        if (billing != null) {
            billing.setIdentifier(identifier);
        }

        if (shipping != null) {
            shipping.setIdentifier(identifier);
        }

        if (billing != null || shipping != null) {
            addressService.update(shipping, billing);
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");

        return customerDto;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBilling(addressService.findByIdentifierAndBilling(identifier));
        customerDto.setShipping(addressService.findByIdentifierAndShipping(identifier));
        return customerDto;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Page<Customer> page =
                customerRepository.findAll(pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();

        wsDto.setContent(
                modelMapper.map(
                        page.getContent(),
                        listType
                )
        );

        wsDto.setTotalRecords(
                page.getTotalElements()
        );

        wsDto.setTotalPages(
                page.getTotalPages()
        );

        wsDto.setSizePerPage(
                page.getSize()
        );

        wsDto.setPage(
                page.getNumber()
        );

        return wsDto;
    }

    @Override
    public List<CustomerDto> findAll() {
        Type listtype = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findAll(), listtype);
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (customer != null) {
            customer.setDeleted(true);
            customerRepository.save(customer);
        }
    }

    @Override
    public Page<CustomerDto> findAll(Pageable pageable, String search) {
        Page<Customer> customers;

        if (search != null && !search.trim().isEmpty()) {
            Specification<Customer> specification =
                    buildGlobalSearchSpec(Customer.class, search);
            customers = customerRepository.findAll(specification, pageable);
        } else {
            customers = customerRepository.findByDeletedFalse(pageable);
        }

        return customers.map(customer ->
                modelMapper.map(customer, CustomerDto.class));
    }
}

