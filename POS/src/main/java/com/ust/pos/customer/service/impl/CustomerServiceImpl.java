package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.impl.AddressServiceImpl;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
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

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressServiceImpl addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressServiceImpl addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with identifier '" + identifier + "' not found");
        }
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
            if (existingCustomer.isDeleted()) {
                customerDto.setMessage("Customer with identifier " + identifier + " was previously deleted. " +
                        "Please contact backend team to restore."
                );
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage("Customer with identifier - " + identifier +
                    " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);
        customerRepository.save(customer);

        AddressDto billingAddress = customerDto.getBillingAddress();
        billingAddress.setIdentifier(customerDto.getIdentifier() + "_" + "Billing");
        billingAddress.setAddressType("Billing");
        billingAddress.setPhoneNo(customerDto.getIdentifier());
        addressService.save(billingAddress);

        AddressDto shippingAddress = customerDto.getShippingAddress();
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
        setAuditFields(existingCustomer, false);
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
        setAuditFields(customer, false);
        customerRepository.save(customer);
        addressService.delete(identifier);
        return true;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        WsDto<CustomerDto> customerWsDto = new WsDto<>();
        customerWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerWsDto.setTotalRecords(customerPage.getTotalElements());
        customerWsDto.setTotalPages(customerPage.getTotalPages());
        customerWsDto.setSizePerPage(pageable.getPageSize());
        customerWsDto.setPage(pageable.getPageNumber());
        return customerWsDto;
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        customer.setStatus(!customer.isStatus());
        setAuditFields(customer, false);
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findIfTrue() {
        Type listType = new TypeToken<List<ShelfsDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public CustomerDto findByIdentifierAndDeletedFalse(String identifier) {
        return modelMapper.map(customerRepository.findByIdentifierAndDeletedFalse(identifier), CustomerDto.class);
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