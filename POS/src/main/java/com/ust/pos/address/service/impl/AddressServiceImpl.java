package com.ust.pos.address.service.impl;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.commonservice.CommonService;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.model.Address;
import com.ust.pos.model.AddressRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl extends CommonService implements AddressService {

    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(
            AddressRepository addressRepository,
            ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void save(AddressDto shipping, AddressDto billing) {
        String shippingIdentifier = shipping.getIdentifier();
        String billingIdentifier = billing.getIdentifier();

        Address existingShipping = addressRepository.
                findByIdentifierAndIsShippingTrueAndIsDeleteFalse(shippingIdentifier);
        Address existingBilling = addressRepository.
                findByIdentifierAndIsBillingTrueAndIsDeleteFalse(billingIdentifier);
        if (existingBilling != null) {
            billing.setMessage("Address with identifier - " + billingIdentifier + " already exists");
            billing.setSuccess(false);
        } else {
            Address address = modelMapper.map(billing, Address.class);
            address.setIsBilling(true);
            address.setIsShipping(false);
            setAuditFields(address, true);
            addressRepository.save(address);
        }
        if (existingShipping != null) {
            shipping.setMessage("Address with identifier - " + shippingIdentifier + " already exists");
            shipping.setSuccess(false);
        } else {
            Address address = modelMapper.map(shipping, Address.class);
            address.setIsShipping(true);
            address.setIsBilling(false);
            setAuditFields(address, true);
            addressRepository.save(address);
        }
    }

    @Override
    public void update(AddressDto shipping, AddressDto billing) {
        String shippingIdentifier = shipping.getIdentifier();
        String billingIdentifier = billing.getIdentifier();
        Address existingShipping = addressRepository.
                findByIdentifierAndIsShippingTrueAndIsDeleteFalse(shippingIdentifier);
        Address existingBilling = addressRepository.
                findByIdentifierAndIsBillingTrueAndIsDeleteFalse(billingIdentifier);

        if (existingBilling == null) {
            billing.setMessage(
                    "Billing address not found for identifier " + billingIdentifier);
        } else {
            long id = existingBilling.getId();
            modelMapper.map(billing, existingBilling);
            existingBilling.setId(id);
            existingBilling.setIsBilling(true);
            existingBilling.setIsShipping(false);
            setAuditFields(existingBilling, false);
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
            setAuditFields(existingShipping, false);
            addressRepository.save(existingShipping);
        }
    }

    @Override
    public void delete(String identifier) {

        Address shippingAddress =
                addressRepository.findByIdentifierAndIsShippingTrueAndIsDeleteFalse(identifier);

        if (shippingAddress != null) {
            shippingAddress.setDelete(true);
            setAuditFields(shippingAddress, false);
            addressRepository.save(shippingAddress);
        }

        Address billingAddress =
                addressRepository.findByIdentifierAndIsBillingTrueAndIsDeleteFalse(identifier);

        if (billingAddress != null) {
            billingAddress.setDelete(true);
            setAuditFields(billingAddress, false);
            addressRepository.save(billingAddress);
        }
    }

    @Override
    public List<AddressDto> findAll() {
        Type listOfType = new TypeToken<List<AddressDto>>() {
        }.getType();
        return modelMapper.map(addressRepository.findAll(), listOfType);
    }

    @Override
    public AddressDto findByIdentifierAndShipping(String identifier) {
        Address address =
                addressRepository.
                        findByIdentifierAndIsShippingTrueAndIsDeleteFalse(identifier);
        if (address == null) {
            return new AddressDto();
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public AddressDto findByIdentifierAndBilling(String identifier) {
        Address address =
                addressRepository.
                        findByIdentifierAndIsBillingTrueAndIsDeleteFalse(identifier);
        if (address == null) {
            return new AddressDto();
        }
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> findAll(Pageable pageable) {
        Type listOfType = new TypeToken<List<AddressDto>>() {
        }.getType();
        Page<Address> brandPage = addressRepository.findAll(pageable);
        return modelMapper.map(brandPage.getContent(), listOfType);
    }
}
