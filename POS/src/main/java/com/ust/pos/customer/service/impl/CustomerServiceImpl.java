package com.ust.pos.customer.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;

import static java.lang.Character.getType;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    private final ModelMapper modelMapper;

    private final AddressService addressService;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, AddressService addressService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.addressService = addressService;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        Customer existingcustomer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if(existingcustomer != null)
        {
            customerDto.setMessage("Customer already exists");
            customerDto.setSuccess(false);
            return customerDto;
        }
        billing.setIdentifier(customerDto.getIdentifier());
        shipping.setIdentifier(customerDto.getIdentifier());
        addressService.save(shipping, billing);
        Customer customer = modelMapper.map(customerDto, Customer.class);
        customer.setDeleted(false);
        customerRepository.save(customer);
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        Customer existingcustomer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        AddressDto billing = customerDto.getBilling();
        AddressDto shipping = customerDto.getShipping();
        if(existingcustomer == null)
        {
            customerDto.setMessage("Customer not found");
            customerDto.setSuccess(false);
            return customerDto;
        }
        modelMapper.map(customerDto, existingcustomer);
        customerRepository.save(existingcustomer);
        if(billing != null)
        {
            billing.setIdentifier(customerDto.getIdentifier());
        }
        if(shipping != null)
        {
            shipping.setIdentifier(customerDto.getIdentifier());
        }
        if (billing != null && shipping != null) {
            addressService.update(shipping, billing);
        }
        return customerDto;
    }

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        CustomerDto customerDto = modelMapper.map(customer,CustomerDto.class);
        customerDto.setBilling(addressService.findByIdentifierAndBilling(identifier));
        customerDto.setShipping(addressService.findByIdentifierAndShipping(identifier));
        return customerDto;
    }

    @Override
    public List<CustomerDto> findAll()
    {
        Type listtype = new TypeToken<List<CustomerDto>>(){}.getType();
        return modelMapper.map(customerRepository.findByDeletedFalse(), listtype);

    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listtype = new TypeToken<List<CustomerDto>>(){}.getType();
        Page<Customer> customerPage = customerRepository.findByDeletedFalse(pageable);

        WsDto<CustomerDto> customerDtoWsDto = new WsDto<>();
        customerDtoWsDto.setDtoList(modelMapper.map(customerPage.getContent(), listtype));
        customerDtoWsDto.setTotalRecords(customerPage.getTotalElements());
        customerDtoWsDto.setTotalPage(customerPage.getTotalPages());
        customerDtoWsDto.setSizePerPage(pageable.getPageSize());
        customerDtoWsDto.setPage(pageable.getPageNumber());
        return customerDtoWsDto;
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        if(customer != null)
        {
            customer.setDeleted(true);
            customerRepository.save(customer);
            addressService.delete(identifier);
        }
    }

    @Override
    public Page<CustomerDto> findAll(String search, Pageable pageable) {
        Page<Customer> rolePage;
        if(search != null && !search.trim().isEmpty())
        {
            rolePage = customerRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse(search, pageable);
        }
        else
        {
            rolePage = customerRepository.findByDeletedFalse(pageable);
        }
        return rolePage.map(customer -> modelMapper.map(customer, CustomerDto.class));
    }

    @Override
    public CustomerDto findByEmail(String email)
    {
        Customer customer = customerRepository.findByEmailAndDeletedFalse(email);
        if(customer == null)
        {
            return null;
        }
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public void toggleStatus(String email)
    {
        Customer customer =
                customerRepository.findByEmailAndDeletedFalse(email);

        if(customer != null)
        {
            customer.setStatus(!customer.getStatus());
            customerRepository.save(customer);
        }
    }
}
