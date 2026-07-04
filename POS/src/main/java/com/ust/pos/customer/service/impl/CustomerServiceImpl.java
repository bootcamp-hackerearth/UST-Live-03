package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
import com.ust.pos.model.Address;
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

    private static final String VALIDATION_MESSAGE = "Customer with identidier - ";
    private static final String SHIPPING_ADDRESS = "shippingAddress";
    private static final String BILLING_ADDRESS = "billingAddress";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final CartService cartService;


    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService, CartService cartService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
        this.cartService = cartService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            throw new ResourseNotFoundException("Data cannot found");
        }

        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);

        if (existingCustomer != null) {
            customerDto.setMessage(
                    existingCustomer.isDeleted()
                            ? VALIDATION_MESSAGE + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : VALIDATION_MESSAGE + identifier
                            + " already exists."
            );
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        if (customerDto.getBillingAddress() != null) {
            billingAddress.setPhoneNo(customerDto.getPhoneNo());
            billingAddress.setAddressType(BILLING_ADDRESS);
            addressService.save(billingAddress);
        }
        if (customerDto.getShippingAddress() != null) {
            shippingAddress.setPhoneNo(customerDto.getPhoneNo());
            shippingAddress.setAddressType(SHIPPING_ADDRESS);
            addressService.save(shippingAddress);
        }

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier(customerDto.getIdentifier());
        cartService.save(cartDto);

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
            customerDto.setMessage(VALIDATION_MESSAGE + identifier + " not found");
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        AddressDto shippingAddress = customerDto.getShippingAddress();

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        billingAddress.setAddressType(BILLING_ADDRESS);

        shippingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setAddressType(SHIPPING_ADDRESS);

        addressService.update(billingAddress);
        addressService.update(shippingAddress);

        customerDto.setBillingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), BILLING_ADDRESS));
        customerDto.setShippingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), SHIPPING_ADDRESS));

        modelMapper.map(customerDto, existingCustomer);
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);

        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier, Long phoneNo) {

        Customer customer = customerRepository.findByIdentifier(identifier);
        List<Address> addresses = addressService.findByPhoneNo(phoneNo);

        setModifiedDetails(customer);
        softDelete(customer);
        for (Address address : addresses) {
            setModifiedDetails(address);
            softDelete(address);
        }
    }

    @Override
    public WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> page = customerRepository.findAll(example, pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setContent(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {


        Page<Customer> customerPage = customerRepository.findByIsDeletedFalse(pageable);

        WsDto<CustomerDto> paginationResponseDto = new WsDto<>();

        List<CustomerDto> customerDtos = customerPage.getContent()
                .stream()
                .map(product -> modelMapper.map(product, CustomerDto.class))
                .toList();

        paginationResponseDto.setContent(customerDtos);
        paginationResponseDto.setPage(customerPage.getNumber());
        paginationResponseDto.setSizePerPage(customerPage.getSize());
        paginationResponseDto.setTotalPages(customerPage.getTotalPages());
        paginationResponseDto.setTotalRecords(customerPage.getTotalElements());

        return paginationResponseDto;
    }

    @Override
    public void toggleStatus(String identifier) {

        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            customerRepository.save(customer);
        }
    }

    @Override
    public List<Customer> findActiveCustomer() {

        return customerRepository.findByStatus(true);
    }
}



