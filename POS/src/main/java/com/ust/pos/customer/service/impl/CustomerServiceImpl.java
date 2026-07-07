package com.ust.pos.customer.service.impl;

import com.ust.pos.adress.service.AddressService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.common.CommonService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
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
    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";
    private static final String BILLING = "billing";
    private static final String SHIPPING = "shipping";
    private final CustomerRepository customerRepository;
    private final AddressService addressService;
    private final ModelMapper modelMapper;
    private final CartService cartService;

    public CustomerServiceImpl(CustomerRepository customerRepository, AddressService addressService,
                               ModelMapper modelMapper, CartService cartService) {
        this.customerRepository = customerRepository;
        this.addressService = addressService;
        this.modelMapper = modelMapper;
        this.cartService = cartService;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (!existingCustomer.isDeleted()) {
                customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " already exists");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " exists and was previously deleted. " +
                    "Please contact backend team to restore.");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);
        customerRepository.save(customer);
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingAddress = customerDto.getBillingAddress();
            String billIdentifier = customerDto.getUsername() + "_billing_" + identifier;
            billingAddress.setIdentifier(billIdentifier);
            billingAddress.setPhoneNo(identifier);
            billingAddress.setCustomerName(customer.getCustomerName());
            billingAddress.setAddressType(BILLING);
            addressService.save(billingAddress);
        }
        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingAddress = customerDto.getShippingAddress();
            String shipIdentifier = customerDto.getUsername() + "_shipping_" + identifier;
            shippingAddress.setIdentifier(shipIdentifier);
            shippingAddress.setPhoneNo(identifier);
            shippingAddress.setCustomerName(customer.getCustomerName());
            shippingAddress.setAddressType(SHIPPING);
            addressService.save(shippingAddress);
        }
        CartDto custCartDto = new CartDto();
        custCartDto.setIdentifier(customer.getIdentifier());
        cartService.save(custCartDto);
        customerDto.setSuccess(true);
        customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " Added Successfully");
        return customerDto;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);
        Type type = new TypeToken<List<CustomerDto>>() {
        }.getType();
        WsDto<CustomerDto> customerWsDto = new WsDto<>();
        customerWsDto.setDtoList(modelMapper.map(customerPage.getContent(), type));
        customerWsDto.setTotalRecords(customerPage.getTotalElements());
        customerWsDto.setTotalPages(customerPage.getTotalPages());
        customerWsDto.setSizePerPage(pageable.getPageSize());
        customerWsDto.setPage(pageable.getPageNumber());
        return customerWsDto;
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

    @Override
    public List<CustomerDto> findAllActive() {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        return modelMapper.map(customerRepository.findAllByStatusAndDeletedFalse(true), listType);
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with identifier '" + identifier + "' not found");
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        List<AddressDto> addresses = addressService.findAllByPhoneNumber(customerDto.getIdentifier());
        customerDto.setBillingAddress(addresses.stream()
                .filter(addr -> BILLING.equalsIgnoreCase(addr.getAddressType()))
                .findFirst()
                .orElse(null));
        customerDto.setShippingAddress(addresses.stream()
                .filter(addr -> SHIPPING.equalsIgnoreCase(addr.getAddressType()))
                .findFirst()
                .orElse(null));
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
        setAuditFields(existingCustomer, false);
        customerRepository.save(existingCustomer);
        List<AddressDto> existingAddresses = addressService.findAllByPhoneNumber(identifier);
        AddressDto existingBilling = null;
        AddressDto existingShipping = null;
        for (AddressDto addressDto : existingAddresses) {
            if (BILLING.equalsIgnoreCase(addressDto.getAddressType())) {
                existingBilling = addressDto;
            } else if (SHIPPING.equalsIgnoreCase(addressDto.getAddressType())) {
                existingShipping = addressDto;
            }
        }
        if (customerDto.getBillingAddress() != null) {
            AddressDto billingDto = customerDto.getBillingAddress();
            if (existingBilling != null) {
                billingDto.setIdentifier(existingBilling.getIdentifier());
            }
            billingDto.setPhoneNo(identifier);
            billingDto.setCustomerName(existingCustomer.getCustomerName());
            billingDto.setAddressType(BILLING);
            addressService.update(billingDto);
        }
        if (customerDto.getShippingAddress() != null) {
            AddressDto shippingDto = customerDto.getShippingAddress();
            if (existingShipping != null) {
                shippingDto.setIdentifier(existingShipping.getIdentifier());
            }
            shippingDto.setPhoneNo(identifier);
            shippingDto.setCustomerName(existingCustomer.getCustomerName());
            shippingDto.setAddressType(SHIPPING);
            addressService.update(shippingDto);
        }
        customerDto.setSuccess(true);
        customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + identifier + " Updated");
        return customerDto;
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
    public boolean delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) return false;
        softDelete(customer);
        setAuditFields(customer, false);
        customerRepository.save(customer);
        addressService.delete(identifier);
        return true;
    }
}
