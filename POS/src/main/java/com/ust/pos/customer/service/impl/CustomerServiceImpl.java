package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String CUSTOMER = "Customer with identifier - ";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
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
            if (Boolean.TRUE.equals(existingCustomer.getDeleted())) {
                customerDto.setMessage(CUSTOMER + identifier + " is not available. Please contact administrator");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage(CUSTOMER + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (billingAddress != null) {
            billingAddress.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(billingAddress);
        }
        if (shippingAddress != null) {
            shippingAddress.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(shippingAddress);
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
        setModifiedDetails(customer);
        customerRepository.save(customer);
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (existingCustomer == null) {
            customerDto.setMessage(CUSTOMER + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        if (billingAddress != null) {
            customerDto.setBillingAddress(addressService.
                    findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "billingAddress"));
            billingAddress.setPhoneNo(customerDto.getPhoneNo());
        }

        if (shippingAddress != null) {
            customerDto.setShippingAddress(addressService.
                    findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), "shippingAddress"));
            shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        }
        modelMapper.map(customerDto, existingCustomer);
        customerRepository.save(existingCustomer);
        addressService.update(billingAddress);
        addressService.update(shippingAddress);
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer" + identifier + "not found");
        }
        Long phoneNo = customer.getPhoneNo();
        softDelete(customer);
        setModifiedDetails(customer);
        customerRepository.save(customer);
        addressService.deleteByPhone(phoneNo);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAllByDeletedFalse(pageable);

        WsDto<CustomerDto> customerWsDto = new WsDto<>();
        customerWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerWsDto.setTotalRecords(customerPage.getTotalElements());
        customerWsDto.setTotalPages(customerPage.getTotalPages());
        customerWsDto.setSizePerPage(pageable.getPageSize());
        customerWsDto.setPage(pageable.getPageNumber());
        return customerWsDto;
    }
}



