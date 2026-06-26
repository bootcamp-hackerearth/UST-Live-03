package com.ust.pos.api.address;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.AddressDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/address")
public class AddressApiController extends BaseController {

    private final AddressService addressService;

    public AddressApiController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/get")
    public AddressDto update(String phoneNo, String addressType) {
        return addressService.findByPhoneNoAndAddressType(phoneNo, addressType);
    }
}
