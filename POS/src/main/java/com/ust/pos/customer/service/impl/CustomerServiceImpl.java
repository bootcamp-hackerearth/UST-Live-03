package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.exception.ResourceNotFoundException;
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
    private final CustomerRepository customerRepository;
    private final AddressService addressService;
    private final ModelMapper modelMapper;
    private final CartService cartService;

    public CustomerServiceImpl(CustomerRepository customerRepository, AddressService addressService, ModelMapper modelMapper, CartService cartService) {
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
            customerDto.setMessage("Customer with identifier - " + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
        customerRepository.save(customer);

        setAddressFields(customerDto);

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
        modelMapper.map(customerDto, existingCustomer);
        setModifiedDetails(existingCustomer);
        customerRepository.save(existingCustomer);

        List<AddressDto> addressDtoList = addressService.findByPhoneNo(identifier);

        if(addressDtoList.isEmpty()){
            setAddressFields(customerDto);
        }
        else {
            Address billingAddress = customerDto.getBillingAddress();
            billingAddress.setIdentifier(addressDtoList.get(0).getIdentifier());
            billingAddress.setAddressType(addressDtoList.get(0).getAddressType());
            addressService.update(modelMapper.map(billingAddress, AddressDto.class));

            Address shippingAddress = customerDto.getShippingAddress();
            shippingAddress.setIdentifier(addressDtoList.get(1).getIdentifier());
            shippingAddress.setAddressType(addressDtoList.get(1).getAddressType());
            addressService.update(modelMapper.map(shippingAddress, AddressDto.class));
        }

        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        softDelete(customer);
        setModifiedDetails(customer);
        addressService.delete(identifier);
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
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndIsDeletedFalse(identifier);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer with identifier '" + identifier + "' not found");
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String identifier) {
        CustomerDto customerDto = findByIdentifier(identifier);
        List<AddressDto> addressDtoList = addressService.findByPhoneNo(identifier);
        if(!addressDtoList.isEmpty()){
            customerDto.setBillingAddress(modelMapper.map(addressDtoList.get(0), Address.class));
            customerDto.setShippingAddress(modelMapper.map(addressDtoList.get(1), Address.class));
        }
        return customerDto;
    }

    private void setAddressFields(CustomerDto customerDto){
        if(customerDto.getBillingAddress()!=null) {
            AddressDto billingAddress = modelMapper.map(customerDto.getBillingAddress(), AddressDto.class);
            billingAddress.setPhoneNo(customerDto.getIdentifier());
            billingAddress.setAddressType("billing");
            billingAddress.setIdentifier(customerDto.getIdentifier() + "_" + billingAddress.getAddressType() + "_" + billingAddress.getZipcode());
            addressService.save(billingAddress);
        }

        if(customerDto.getShippingAddress()!=null) {
            AddressDto shippingAddress = modelMapper.map(customerDto.getShippingAddress(), AddressDto.class);
            shippingAddress.setPhoneNo(customerDto.getIdentifier());
            shippingAddress.setAddressType("shipping");
            shippingAddress.setIdentifier(customerDto.getIdentifier() + "_" + shippingAddress.getAddressType() + "_" + shippingAddress.getZipcode());
            addressService.save(shippingAddress);
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
