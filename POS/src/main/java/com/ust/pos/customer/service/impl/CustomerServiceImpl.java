package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
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
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";
    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

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

            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
            if (existingCustomer.isDeleted()) {
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " was deleted , Please Contact the Administrator to add.");
            }
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
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
        customerDto.setBillingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "billingAddress"));
        customerDto.setShippingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "shippingAddress"));
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        softDelete(customer);
        setModifiedDetails(customer);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByIsDeletedFalse(pageable);
        WsDto<CustomerDto> customerWsDto = new WsDto<>();
        customerWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerWsDto.setTotalRecords(customerPage.getTotalElements());
        customerWsDto.setTotalPages(customerPage.getTotalPages());
        customerWsDto.setSizePerPage(pageable.getPageSize());
        customerWsDto.setPage(pageable.getPageNumber());

        return customerWsDto;
    }

    @Override
    public String buildAddressIdentifier(AddressDto address) {
        if (address == null) return null;
        return address.getAddressline().trim().toUpperCase()
                + "-" + address.getZipcode()
                + "-" + address.getAddressType().toUpperCase();
    }

    @Override
    public void toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            setModifiedDetails(customer);
            customerRepository.save(customer);
        }
    }

    @Override
    public WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> page = customerRepository.findAll(example, pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }
}
