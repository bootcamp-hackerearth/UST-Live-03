package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.common.CommonService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl extends CommonService implements CustomerService {

    private static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";

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
        customerDto.setIdentifier(customerDto.getIdentifier().trim());
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (!existingCustomer.isDeleted()) {
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " was previously deleted. " +
                    "Please contact backend team to restore.");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer,true);
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
        customerDto.setSuccess(true);
        customerDto.setMessage("Customer created successfully");
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
        modelMapper.map(customerDto, existingCustomer);
        setAuditFields(existingCustomer,false);
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
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) return false;
        softDelete(customer);
        setAuditFields(customer,false);
        customerRepository.save(customer);
        addressService.delete(identifier);
        return true;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        WsDto<CustomerDto> customerDtoWsDto = new WsDto<>();
        customerDtoWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerDtoWsDto.setTotalRecords(customerPage.getTotalElements());
        customerDtoWsDto.setTotalPages(customerPage.getTotalPages());
        customerDtoWsDto.setSizePerPage(pageable.getPageSize());
        customerDtoWsDto.setPage(pageable.getPageNumber());
        return customerDtoWsDto;
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        setAuditFields(customer,false);
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findIfTrue() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }
}