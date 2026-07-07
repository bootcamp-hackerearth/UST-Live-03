package com.ust.pos.customer.service.impl;

import com.ust.pos.CommonService;
import com.ust.pos.address.service.AddressService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
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

    private static final String CUSTOMER_WITH_PHONE = "Customer with phone - ";
    private static final String NOT_FOUND = " not found";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto findById(String phoneNo) {
        return modelMapper.map(customerRepository.findByPhoneNo(phoneNo), CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String phoneNo) {

        Customer customer = customerRepository.findByPhoneNo(phoneNo);

        if (customer == null) {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_PHONE + phoneNo + NOT_FOUND);
            return customerDto;
        }

        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);

        List<AddressDto> list = addressService.findAllByPhoneNo(phoneNo);

        if (list != null) {
            if (!list.isEmpty()) {
                customerDto.setBillingAddress(list.get(0));
            }
            if (list.size() > 1) {
                customerDto.setShippingAddress(list.get(1));
            }
        }

        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {

        if (customerDto == null) {
            return null;
        }

        if (customerDto.getPhoneNo() == null || customerDto.getPhoneNo().length() != 10) {
            customerDto.setSuccess(false);
            customerDto.setMessage("Invalid phone number");
            return customerDto;
        }

        String phone = customerDto.getPhoneNo();

        Customer existing = customerRepository.findByPhoneNo(phone);

        if (existing != null && !existing.getDeleted()) {
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_PHONE + phone + " already exists");
            return customerDto;
        }

        if (existing != null && existing.getDeleted()) {
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_PHONE + phone + " was previously deleted. Please contact backend team to restore.");
            return customerDto;
        }

        customerDto.setIdentifier(phone);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setAuditFields(customer, true);

        customerRepository.save(customer);

        if (customerDto.getBillingAddress() != null) {
            AddressDto billing = customerDto.getBillingAddress();
            billing.setIdentifier(phone + "_Billing");
            billing.setPhoneNo(phone);
            billing.setAddressType("Billing");
            addressService.save(billing);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shipping = customerDto.getShippingAddress();
            shipping.setIdentifier(phone + "_Shipping");
            shipping.setPhoneNo(phone);
            shipping.setAddressType("Shipping");
            addressService.save(shipping);
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer created successfully");

        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {

        String phone = customerDto.getPhoneNo();
        Customer existing = customerRepository.findByPhoneNo(phone);

        if (existing == null) {
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_PHONE + phone + NOT_FOUND);
            return customerDto;
        }

        if (existing.getDeleted()) {
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_PHONE + phone + " was previously deleted. Please contact backend team to restore.");
            return customerDto;
        }

        modelMapper.map(customerDto, existing);
        setAuditFields(existing, false);

        customerRepository.save(existing);

        if (customerDto.getBillingAddress() != null) {
            AddressDto billing = customerDto.getBillingAddress();
            billing.setPhoneNo(phone);
            billing.setAddressType("Billing");
            billing.setIdentifier(phone + "_Billing");
            addressService.update(billing);
        }

        if (customerDto.getShippingAddress() != null) {
            AddressDto shipping = customerDto.getShippingAddress();
            shipping.setPhoneNo(phone);
            shipping.setAddressType("Shipping");
            shipping.setIdentifier(phone + "_Shipping");
            addressService.update(shipping);
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");

        return customerDto;
    }

    @Override
    public boolean delete(String phoneNo) {

        Customer customer = customerRepository.findByPhoneNo(phoneNo);

        if (customer == null) {
            return false;
        }

        softDelete(customer);
        setAuditFields(customer, false);

        customerRepository.save(customer);
        addressService.delete(phoneNo);

        return true;
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {

        Type listType = new TypeToken<List<CustomerDto>>() {
        }.getType();

        Page<Customer> page = customerRepository.findByDeletedFalse(pageable);

        WsDto<CustomerDto> wsDto = new WsDto<>();
        wsDto.setDtoList(modelMapper.map(page.getContent(), listType));
        wsDto.setTotalRecords(page.getTotalElements());
        wsDto.setTotalPages(page.getTotalPages());
        wsDto.setSizePerPage(pageable.getPageSize());
        wsDto.setPage(pageable.getPageNumber());

        return wsDto;
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
    public CustomerDto toggleStatus(String phoneNo) {

        Customer customer = customerRepository.findByPhoneNo(phoneNo);

        if (customer == null) {
            CustomerDto customerDto = new CustomerDto();
            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_PHONE + phoneNo + NOT_FOUND);
            return customerDto;
        }

        customer.setStatus(!customer.getStatus());
        setAuditFields(customer, false);

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

