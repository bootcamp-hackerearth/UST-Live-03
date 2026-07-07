package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
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
@Transactional
public class CustomerServiceImpl extends BaseService implements CustomerService {
    public static final String BILLING = "billing";
    public static final String SHIPPING = "shipping";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               ModelMapper modelMapper,
                               AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer =
                customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if (customer == null) {
            return null;
        }
        CustomerDto dto = modelMapper.map(customer, CustomerDto.class);
        dto.setBillingAddress(
                addressService.findByPhoneNoAndAddressType(
                        customer.getPhoneNo(),
                        BILLING
                )
        );
        dto.setShippingAddress(
                addressService.findByPhoneNoAndAddressType(
                        customer.getPhoneNo(),
                        SHIPPING
                )
        );
        return dto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (Boolean.TRUE.equals(existingCustomer.getDeleted())) {
                customerDto.setMessage("Customer - " + identifier + " was deleted and cannot be recreated");
            } else {
                customerDto.setMessage("Customer with identifier - " + identifier + " already exists");
            }
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();
        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        billingAddress.setAddressType(BILLING);
        shippingAddress.setAddressType(SHIPPING);
        addressService.save(billingAddress);
        addressService.save(shippingAddress);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
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
            customerDto.setMessage(
                    "Customer with identifier - " + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (billingAddress != null) {
            billingAddress.setPhoneNo(customerDto.getPhoneNo());
            billingAddress.setAddressType(BILLING);
            addressService.update(billingAddress);
        }
        if (shippingAddress != null) {
            shippingAddress.setPhoneNo(customerDto.getPhoneNo());
            shippingAddress.setAddressType(SHIPPING);
            addressService.update(shippingAddress);
        }
        modelMapper.map(customerDto, existingCustomer);
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        customerDto.setSuccess(true);
        return customerDto;
    }

    @Override
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if(customer==null){
            return;
        }
        Long phoneNo = customer.getPhoneNo();
            softDelete(customer);
            setModifiedDetails(customer);
            customerRepository.save(customer);
            addressService.deleteByPhone(phoneNo);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        List<CustomerDto> customerDtos = customerPage.getContent()
                .stream()
                .map(customer -> {
                    CustomerDto dto = modelMapper.map(customer, CustomerDto.class);
                    dto.setBillingAddress(addressService.findByPhoneNoAndAddressType(
                            customer.getPhoneNo(), BILLING)
                    );
                    dto.setShippingAddress(
                            addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), SHIPPING)
                    );
                    return dto;
                })
                .toList();
        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(customerDtos);
        wsDto.setTotalRecords(customerPage.getTotalElements());
        wsDto.setTotalPages(customerPage.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        return wsDto;
    }

    @Override
    public String buildAddressIdentifier(AddressDto address) {
        if (address == null) {
            return null;
        }
        return address.getAddressLine().trim().toUpperCase()
                + "-" + address.getZipcode()
                + "-" + address.getAddressType().toUpperCase();
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