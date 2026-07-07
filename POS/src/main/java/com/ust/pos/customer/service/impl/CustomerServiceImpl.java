package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Customer;
import com.ust.pos.modell.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends BaseService implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    @Override
    public CustomerDto findById(String identifier) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String phoneNo) {
        Customer customer = customerRepository.findByPhoneNoAndDeletedFalse(phoneNo);

        if (customer == null) {
            return null;
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setIdentifier(customer.getIdentifier());
        List<AddressDto> addressDtoList = addressService.findAllByPhoneNo(phoneNo);

        if (addressDtoList != null && !addressDtoList.isEmpty()) {
            customerDto.setBillingAddress(addressDtoList.get(0));
            if (addressDtoList.size() > 1) {
                customerDto.setShippingAddress(addressDtoList.get(1));
            }
        }
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByPhoneNo(customerDto.getPhoneNo());

        if (existingCustomer != null) {
            if (Boolean.TRUE.equals(existingCustomer.getDeleted())) {
                customerDto.setMessage("Customer with phoneNo " + customerDto.getPhoneNo() + " already exists (Soft-Deleted)");
                customerDto.setSuccess(false);
                return customerDto;
            }

            customerDto.setMessage("Customer already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);
        customer.setIdentifier(identifier);

        if (customer.getStatus() == null) {
            customer.setStatus(true);
        }
        setCreatedDetails(customer);
        customerRepository.save(customer);

        if (customerDto.getBillingAddress() != null) {
            AddressDto billing = customerDto.getBillingAddress();
            billing.setIdentifier(identifier + "_Billing");
            billing.setAddressType("Billing");
            billing.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(billing);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shipping = customerDto.getShippingAddress();
            shipping.setIdentifier(identifier + "_Shipping");
            shipping.setAddressType("Shipping");
            shipping.setPhoneNo(customerDto.getPhoneNo());
            addressService.save(shipping);
        }
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findByPhoneNoAndDeletedFalse(customerDto.getPhoneNo());

        if (existingCustomer == null) {
            customerDto.setMessage("Customer with phoneNo - " + customerDto.getPhoneNo() + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        String originalCreatedBy = existingCustomer.getCreatedBy();
        java.time.LocalDateTime originalCreatedOn = existingCustomer.getCreatedOn();
        modelMapper.map(customerDto, existingCustomer);
        existingCustomer.setCreatedBy(originalCreatedBy);
        existingCustomer.setCreatedOn(originalCreatedOn);
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        String phoneNo = existingCustomer.getPhoneNo();

        if (customerDto.getBillingAddress() != null) {
            AddressDto billing = customerDto.getBillingAddress();
            billing.setPhoneNo(phoneNo);
            billing.setAddressType("Billing");
            billing.setIdentifier(phoneNo + "_Billing");
            addressService.update(billing);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shipping = customerDto.getShippingAddress();
            shipping.setPhoneNo(phoneNo);
            shipping.setAddressType("Shipping");
            shipping.setIdentifier(phoneNo + "_Shipping");
            addressService.update(shipping);
        }
        return customerDto;
    }

    @Override
    public boolean delete(String phoneNo) {
        Customer customer = customerRepository.findByPhoneNoAndDeletedFalse(phoneNo);

        if (customer != null) {
            softDelete(customer);
            setModifiedDetails(customer);
            customerRepository.save(customer);
            addressService.delete(phoneNo);
            return true;
        }
        return false;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> customerPage = customerRepository.findAllByDeletedFalse(pageable);
        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        wsDto.setTotalRecords(customerPage.getTotalElements());
        wsDto.setTotalPage(customerPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdAndDeletedFalse(identifier);
        if (customer != null) {
            customer.setStatus(!customer.getStatus());
            setModifiedDetails(customer);
            customerRepository.save(customer);
            return modelMapper.map(customer, CustomerDto.class);
        }
        return null;
    }

    @Override
    public List<CustomerDto> findIfTrue() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findByStatusIsTrueAndDeletedFalse(), listType);
    }

    @Override
    public WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> page = customerRepository.findAll(example, pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPage(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

}
