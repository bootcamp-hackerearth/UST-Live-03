package com.ust.pos.customer.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.base.service.BaseService;
import com.ust.pos.customer.service.CustomerService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Customer;
import com.ust.pos.models.CustomerRepository;
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

    public static final String CUSTOMER_WITH_IDENTIFIER = "Customer with identifier - ";
    public static final String BILLING = "Billing";
    public static final String SHIPPING = "Shipping";

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final AddressService addressService;

    @Override
    public CustomerDto findByIdentifier(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);

        if (customer == null)
            return null;
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto findByIdentifierWithAddressDto(String phoneNo) {
        Customer customer = customerRepository.findByPhoneNo(phoneNo);
        CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        List<AddressDto> addressDtoList = addressService.findAllByPhoneNo(phoneNo);

        if (addressDtoList != null) {

            for (AddressDto addr : addressDtoList) {

                if (BILLING.equalsIgnoreCase(addr.getAddressType())) {
                    customerDto.setBillingAddress(addr);
                }

                if (SHIPPING.equalsIgnoreCase(addr.getAddressType())) {
                    customerDto.setShippingAddress(addr);
                }
            }
        }
        return customerDto;
    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        String identifier = customerDto.getIdentifier();
        String phoneNo = customerDto.getPhoneNo();
        Customer phoneExists = customerRepository.findByPhoneNo(phoneNo);

        if (phoneExists != null) {

            if (Boolean.TRUE.equals(phoneExists.getDeleted())) {
                customerDto.setSuccess(false);
                customerDto.setMessage(
                        "Customer with this phone number was deleted and cannot be reused."
                );
                return customerDto;
            }
            customerDto.setSuccess(false);
            customerDto.setMessage("Customer with this phone number already exists");
            return customerDto;
        }

        Customer existing = customerRepository.findByIdentifierAndDeletedFalse(identifier);

        if (existing != null) {

            customerDto.setSuccess(false);
            customerDto.setMessage(
                    CUSTOMER_WITH_IDENTIFIER + identifier + " already exists"
            );
            return customerDto;
        }

        Customer customer = modelMapper.map(customerDto, Customer.class);
        setCreatedDetails(customer);
        customerRepository.save(customer);

        if (customerDto.getBillingAddress() != null) {

            AddressDto billing = modelMapper.map(customerDto.getBillingAddress(), AddressDto.class);
            billing.setIdentifier(identifier + "_Billing");
            billing.setAddressType(BILLING);
            billing.setPhoneNo(phoneNo);
            addressService.save(billing);
        }

        if (customerDto.getShippingAddress() != null) {

            AddressDto shipping = modelMapper.map(customerDto.getShippingAddress(), AddressDto.class);
            shipping.setIdentifier(identifier + "_Shipping");
            shipping.setAddressType(SHIPPING);
            shipping.setPhoneNo(phoneNo);
            addressService.save(shipping);
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer created successfully");
        return customerDto;
    }

    @Override
    public CustomerDto update(CustomerDto customerDto) {
        Customer existing = customerRepository.findByIdentifierAndDeletedFalse(customerDto.getIdentifier());

        if (existing == null) {

            customerDto.setSuccess(false);
            customerDto.setMessage(CUSTOMER_WITH_IDENTIFIER + customerDto.getIdentifier() + " not found");
            return customerDto;
        }

        existing.setCustomerName(customerDto.getCustomerName());
        existing.setPhoneNo(customerDto.getPhoneNo());
        existing.setPartyType(customerDto.getPartyType());
        existing.setCredit(customerDto.getCredit());
        existing.setCreditLimit(customerDto.getCreditLimit());
        setModifiedDetails(existing);
        customerRepository.save(existing);
        List<AddressDto> addresses = addressService.findAllByPhoneNo(customerDto.getPhoneNo());

        if (addresses != null) {

            for (AddressDto addr : addresses) {

                if (BILLING.equalsIgnoreCase(addr.getAddressType())
                        && customerDto.getBillingAddress() != null) {

                    addr.setAddressLine(customerDto.getBillingAddress().getAddressLine());
                    addr.setCity(customerDto.getBillingAddress().getCity());
                    addr.setState(customerDto.getBillingAddress().getState());
                    addr.setZipCode(customerDto.getBillingAddress().getZipCode());
                    addr.setCountry(customerDto.getBillingAddress().getCountry());
                    addressService.update(addr);
                }

                if (SHIPPING.equalsIgnoreCase(addr.getAddressType()) && customerDto.getShippingAddress() != null) {

                    addr.setAddressLine(customerDto.getShippingAddress().getAddressLine());
                    addr.setCity(customerDto.getShippingAddress().getCity());
                    addr.setState(customerDto.getShippingAddress().getState());
                    addr.setZipCode(customerDto.getShippingAddress().getZipCode());
                    addr.setCountry(customerDto.getShippingAddress().getCountry());
                    addressService.update(addr);
                }
            }
        }

        customerDto.setSuccess(true);
        customerDto.setMessage("Customer updated successfully");
        return customerDto;
    }

    @Override
    public void delete(String phoneNo) {
        Customer customer = customerRepository.findByPhoneNoAndDeletedFalse(phoneNo);

        if (customer == null) return;

        softDelete(customer);
        setModifiedDetails(customer);
        customerRepository.save(customer);
        addressService.delete(phoneNo);
    }

    @Override
    public WsDto<CustomerDto> findAll(Pageable pageable) {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        Page<Customer> page = customerRepository.findAllByDeletedFalse(pageable);
        WsDto<CustomerDto> ws = new WsDto<>();
        ws.setDtoList(modelMapper.map(page.getContent(), listType));
        ws.setTotalRecords(page.getTotalElements());
        ws.setTotalPages(page.getTotalPages());
        ws.setSizePerPage(pageable.getPageSize());
        ws.setPage(pageable.getPageNumber());
        return ws;
    }

    @Override
    public CustomerDto toggleStatus(String identifier) {
        Customer customer = customerRepository.findByIdentifierAndDeletedFalse(identifier);
        customer.setStatus(!customer.getStatus());
        setModifiedDetails(customer);
        customerRepository.save(customer);
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public List<CustomerDto> findIfTrue() {
        Type listType = new TypeToken<List<CustomerDto>>() {}.getType();
        return modelMapper.map(customerRepository.findByStatusIsTrueAndDeletedFalse(), listType
        );
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