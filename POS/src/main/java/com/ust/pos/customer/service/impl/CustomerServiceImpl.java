package com.ust.pos.customer.service.impl;

import com.ust.pos.address.AddressService;
import com.ust.pos.common.CommonService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.PageDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl extends CommonService implements CustomerService {

    public static final String BILLING = "billing";
    public static final String SHIPPING = "shipping";


    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository,AddressService addressService, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService=addressService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return null;
        }

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setBillingAddress(
                addressService.findByPhoneAndAddressType(identifier, BILLING));
        customerDto.setShippingAddress(
                addressService.findByPhoneAndAddressType(identifier, SHIPPING));

        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String phoneNo = customerDto.getPhoneNo();

        Customer existingCustomer = customerRepository.findByIdentifier(phoneNo);
        if (existingCustomer != null) {
            if (Boolean.TRUE.equals(existingCustomer.getDeleted())) {
                customerDto.setMessage("Customer with identifier - " + phoneNo + " has been soft deleted. Restore it by changing status.");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage("Customer with phone number - " + phoneNo + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        if (billingAddress == null) {
            billingAddress = new AddressDto();
            customerDto.setBillingAddress(billingAddress);
        }

        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (shippingAddress == null) {
            shippingAddress = new AddressDto();
            customerDto.setShippingAddress(shippingAddress);
        }

        billingAddress.setPhoneNo(phoneNo);
        billingAddress.setAddressType(BILLING);

        shippingAddress.setPhoneNo(phoneNo);
        shippingAddress.setAddressType(SHIPPING);

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        customer.setIdentifier(phoneNo);
        customer.setDeleted(false);
        customer.setStatus(true);
        setAuditFields(customer, true);
        customerRepository.save(customer);
        customerDto.setSuccess(true);
        return customerDto;
    }

    @Transactional
    @Override
    public CustomerDto update(CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();

        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer == null) {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billing =
                addressService.findByPhoneAndAddressType(identifier, BILLING);
        AddressDto shipping =
                addressService.findByPhoneAndAddressType(identifier, SHIPPING);

        if (billing == null) {
            billing = new AddressDto();
            billing.setPhoneNo(identifier);
            billing.setAddressType(BILLING);
        }
        if (shipping == null) {
            shipping = new AddressDto();
            shipping.setPhoneNo(identifier);
            shipping.setAddressType(SHIPPING);
        }
        if (customerDto.getBillingAddress() != null) {
            modelMapper.map(customerDto.getBillingAddress(), billing);
        }
        if (customerDto.getShippingAddress() != null) {
            modelMapper.map(customerDto.getShippingAddress(), shipping);
        }

        addressService.update(billing);
        addressService.update(shipping);

        modelMapper.map(customerDto, customer);
        setAuditFields(customer,false);
        customerRepository.save(customer);

        customerDto.setSuccess(true);
        return customerDto;
    }

    @Override
    @Transactional
    public boolean delete(String identifier) {

        Customer customer =
                customerRepository.findByIdentifier(identifier);

        if (customer == null || Boolean.TRUE.equals(customer.getDeleted())) {
            return false;
        }

        softDelete(customer);
        setAuditFields(customer, false);

        customerRepository.save(customer);
        addressService.delete(identifier);
        return true;
    }

    @Override
    public PageDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        PageDto<CustomerDto> pageDto = new PageDto<>();
        pageDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        pageDto.setTotalRecords(customerPage.getTotalElements());
        pageDto.setTotalPages(customerPage.getTotalPages());
        pageDto.setSizePerPage(pageable.getPageSize());
        pageDto.setPage(pageable.getPageNumber());
        return pageDto;
    }

    @Override
    public PageDto<CustomerDto> findAll(Specification<Customer> spec, Pageable pageable, String keyword) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAll(spec, pageable);
        PageDto<CustomerDto> PageDto = new PageDto<>();
        PageDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        PageDto.setTotalRecords(customerPage.getTotalElements());
        PageDto.setTotalPages(customerPage.getTotalPages());
        PageDto.setSizePerPage(pageable.getPageSize());
        PageDto.setPage(pageable.getPageNumber());
        PageDto.setKeyword(keyword);
        return PageDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            boolean currentStatus = Boolean.TRUE.equals(customer.getStatus());
            customer.setStatus(!currentStatus);
            customerRepository.save(customer);
        }
    }

    @Override
    public List<CustomerDto> findActiveCustomers() {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        return modelMapper.map(customerRepository.findByStatusTrue(),listType);
    }
}