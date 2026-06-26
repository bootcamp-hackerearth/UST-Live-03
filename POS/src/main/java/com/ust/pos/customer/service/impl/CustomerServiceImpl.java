package com.ust.pos.customer.service.impl;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import com.ust.pos.model.Customer;
import com.ust.pos.model.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class CustomerServiceImpl extends BaseService implements CustomerService {

    public static final String BILLING_ADDRESS = "billingAddress";
    public static final String SHIPPING_ADDRESS = "shippingAddress";
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final CartService cartService;
    private final AddressRepository addressRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper,
                               AddressService addressService, CartService cartService, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
        this.cartService = cartService;
        this.addressRepository = addressRepository;
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
            customerDto.setMessage(
                    existingCustomer.isDeleted()
                            ? " Customer with identifier - " + identifier
                            + " already exists but was deleted, Please contact Administrator."
                            : " Customer with identifier - " + identifier
                            + " already exists."
            );
            customerDto.setSuccess(false);
            return customerDto;
        }

        AddressDto billingAddress = customerDto.getBillingAddress();
        if (billingAddress == null) {
            billingAddress = new AddressDto();
            billingAddress.setAddressType(BILLING_ADDRESS);
            customerDto.setBillingAddress(billingAddress);
        }

        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (shippingAddress == null) {
            shippingAddress = new AddressDto();
            shippingAddress.setAddressType(SHIPPING_ADDRESS);
            customerDto.setShippingAddress(shippingAddress);
        }

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());

        addressService.save(billingAddress);
        addressService.save(shippingAddress);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
        customerRepository.save(customer);

        CartDto cartDto = new CartDto();
        cartDto.setIdentifier(customerDto.getIdentifier());
        cartService.save(cartDto);

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

        AddressDto billingAddress = customerDto.getBillingAddress();
        if (billingAddress == null) {
            billingAddress = new AddressDto();
            billingAddress.setAddressType(BILLING_ADDRESS);
            customerDto.setBillingAddress(billingAddress);
        }

        AddressDto shippingAddress = customerDto.getShippingAddress();
        if (shippingAddress == null) {
            shippingAddress = new AddressDto();
            shippingAddress.setAddressType(SHIPPING_ADDRESS);
            customerDto.setShippingAddress(shippingAddress);
        }

        billingAddress.setPhoneNo(customerDto.getPhoneNo());
        shippingAddress.setPhoneNo(customerDto.getPhoneNo());

        addressService.update(billingAddress);
        addressService.update(shippingAddress);

        modelMapper.map(customerDto, existingCustomer);
        customerDto.setBillingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), BILLING_ADDRESS));
        customerDto.setShippingAddress(addressService.
                findByPhoneNoAndAddressType(existingCustomer.getPhoneNo(), SHIPPING_ADDRESS));

        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);

        if (customer != null) {
            List<Address> addresses = addressRepository.findByPhoneNo(customer.getPhoneNo());

            setModifiedDetails(customer);
            softDelete(customer);
            customerRepository.save(customer);

            for (Address address : addresses) {
                setModifiedDetails(address);
                softDelete(address);
                addressRepository.save(address);
            }
        }
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Page<Customer> customerPage = customerRepository.findByIsDeletedFalse(pageable);

        List<CustomerDto> customerDtos = modelMapper.map(
                customerPage.getContent(),
                listType
        );

        WsDto<CustomerDto> wsDto =
                new WsDto<>();

        wsDto.setContent(customerDtos);
        wsDto.setPage(customerPage.getNumber());
        wsDto.setSizePerPage(customerPage.getSize());
        wsDto.setTotalPages(customerPage.getTotalPages());
        wsDto.setTotalRecords(customerPage.getTotalElements());

        return wsDto;
    }

    @Override
    public void toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer != null) {
            customer.setStatus(!customer.isStatus());
            customerRepository.save(customer);
        }
    }
}