package com.ust.pos.customer.service.impl;
import com.ust.pos.CommonService;
import com.ust.pos.address.service.AddressService;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.*;
import com.ust.pos.model.AddressRepository;
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
public class CustomerServiceImpl extends CommonService implements CustomerService {

    public static final String SHIPPING_ADDRESS = "shippingAddress";
    public static final String BILLING_ADDRESS = "billingAddress";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final CartEntryService cartEntryService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService, AddressRepository addressRepository, CartService cartService, CartEntryService cartEntryService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) {
            return null;
        }
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        customerDto.setShippingAddress(
                addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), SHIPPING_ADDRESS));
        customerDto.setBillingAddress(
                addressService.findByPhoneNoAndAddressType(customer.getPhoneNo(), BILLING_ADDRESS));
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingCustomer = customerRepository.findByIdentifier(identifier);
        if (existingCustomer != null) {
            if (existingCustomer.isDeleted()) {
                customerDto.setMessage("Customer identifier - " + identifier + " not available");
                customerDto.setSuccess(false);
                return customerDto;
            }
            customerDto.setMessage("Customer with identifier - " + identifier + " already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        Long phoneNo = customerDto.getPhoneNo();
        String phoneNoStr = String.valueOf(phoneNo);
        AddressDto billAddr = customerDto.getBillingAddress();
        AddressDto shipAddr = customerDto.getShippingAddress();
        billAddr.setPhoneNo(phoneNo);
        shipAddr.setPhoneNo(phoneNo);
        billAddr.setAddressType(BILLING_ADDRESS);
        shipAddr.setAddressType(SHIPPING_ADDRESS);
        addressService.save(billAddr);
        addressService.save(shipAddr);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);
        customerRepository.save(customer);
        CartDto cartDto = new CartDto();
        cartDto.setIdentifier(phoneNoStr);
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
        Long phoneNo = customerDto.getPhoneNo();
        AddressDto billAddr = customerDto.getBillingAddress();
        AddressDto shipAddr = customerDto.getShippingAddress();
        billAddr.setPhoneNo(phoneNo);
        shipAddr.setPhoneNo(phoneNo);
        billAddr.setAddressType(BILLING_ADDRESS);
        shipAddr.setAddressType(SHIPPING_ADDRESS);
        addressService.update(billAddr);
        addressService.update(shipAddr);
        modelMapper.map(customerDto, existingCustomer);
        setAuditFields(existingCustomer, false);
        customerRepository.save(existingCustomer);
        customerDto.setBillingAddress(addressService.findByPhoneNoAndAddressType(phoneNo, BILLING_ADDRESS));
        customerDto.setShippingAddress(addressService.findByPhoneNoAndAddressType(phoneNo, SHIPPING_ADDRESS));
        return customerDto;
    }

    @Override
    @Transactional
    public void delete(String identifier) {
        Customer customer = customerRepository.findByIdentifier(identifier);
        if (customer == null) { return; }
        String phoneNo = String.valueOf(customer.getPhoneNo());
        cartEntryService.deleteAllByCart(phoneNo);
        cartService.delete(phoneNo);
        addressRepository.deleteByPhoneNo(customer.getPhoneNo());
        softDelete(customer);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        Page<Customer> customerPage = customerRepository.findByIsDeletedFalse(pageable);
        WsDto<CustomerDto> customerDtoWsDto = new WsDto<>();
        customerDtoWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listType));
        customerDtoWsDto.setTotalRecords(customerPage.getTotalElements());
        customerDtoWsDto.setTotalPages(customerPage.getTotalPages());
        customerDtoWsDto.setSizePerPage(pageable.getPageSize());
        customerDtoWsDto.setPage(pageable.getPageNumber());
        return customerDtoWsDto;
    }

    @Override
    public String buildAddressIdentifier(AddressDto address) {
        if (address == null) { return null; }
        return address.getAddressLine().trim().toUpperCase()
                + "-" + address.getZipcode()
                + "-" + address.getAddressType().toUpperCase();
    }

    @Override
    public WsDto<CustomerDto> findAll(Specification<Customer> example, Pageable pageable,String keyword) {
        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();
        Page<Customer> page = customerRepository.findAll(example, pageable);
        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());
        wsDto.setKeyword(keyword);
        return wsDto;
    }
}