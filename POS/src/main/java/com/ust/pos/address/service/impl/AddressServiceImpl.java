package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void save(AddressDto shipping, AddressDto billing) {
        String shippingIdentifier = shipping.getIdentifier();
        String billingIdentifier = billing.getIdentifier();

        Address existingShipping = addressRepository.findByIdentifierAndIsShippingTrue(shippingIdentifier);
        Address existingBilling = addressRepository.findByIdentifierAndIsBillingTrue(billingIdentifier);

        if (existingBilling != null) {
            billing.setMessage("Address with identifier - " + billingIdentifier + " already exists");
            billing.setSuccess(false);
        } else {
            Address address = modelMapper.map(billing, Address.class);
            address.setIsBilling(true);
            address.setIsShipping(false);
            addressRepository.save(address);
        }
        if (existingShipping != null) {
            shipping.setMessage("Address with identifier - " + shippingIdentifier + " already exists");
            shipping.setSuccess(false);
        } else {
            Address address = modelMapper.map(shipping, Address.class);
            address.setIsShipping(true);
            address.setIsBilling(false);
            addressRepository.save(address);
        }
    }

    @Override
    public void update(AddressDto shipping, AddressDto billing) {
        String shippingIdentifier = shipping.getIdentifier();
        String billingIdentifier = billing.getIdentifier();

        Address existingShipping = addressRepository.findByIdentifierAndIsShippingTrue(shippingIdentifier);
        Address existingBilling = addressRepository.findByIdentifierAndIsBillingTrue(billingIdentifier);

        if (existingBilling == null) {
            billing.setMessage(
                    "Billing address not found for identifier " + billingIdentifier);
        } else {
            long id = existingBilling.getId();
            modelMapper.map(billing, existingBilling);
            existingBilling.setId(id);
            existingBilling.setIsBilling(true);
            existingBilling.setIsShipping(false);
            addressRepository.save(existingBilling);
        }
        if (existingShipping == null) {
            shipping.setMessage("Shipping address not found for identifier " + shippingIdentifier);
        } else {
            long id = existingShipping.getId();
            modelMapper.map(shipping, existingShipping);
            existingShipping.setId(id);
            existingShipping.setIsShipping(true);
            existingShipping.setIsBilling(false);
            addressRepository.save(existingShipping);
        }
    }

    @Override
    public void delete(String identifier) {
        addressRepository.deleteByIdentifier(identifier);
    }

    @Override
    public List<AddressDto> findAll() {
        Type listOfType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listOfType);
    }

    @Override
    public AddressDto findByIdentifierAndShipping(String identifier) {
        return modelMapper.map(addressRepository.
                findByIdentifierAndIsShippingTrue(identifier), AddressDto.class);
    }

    @Override
    public AddressDto findByIdentifierAndBilling(String identifier) {
        return modelMapper.map(addressRepository.
                findByIdentifierAndIsBillingTrue(identifier), AddressDto.class);
    }
}