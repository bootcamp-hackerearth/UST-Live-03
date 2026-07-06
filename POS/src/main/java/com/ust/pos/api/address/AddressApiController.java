package com.ust.pos.api.address;

import com.ust.pos.address.service.AddressService;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'CASHIER', 'SUPPORT')")
public class AddressApiController extends BaseController {

    private final AddressService addressService;

    public AddressApiController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/list")
    public List<AddressDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(), paginationDto.getSortDirection(),
                paginationDto.getSortField());
        return addressService.findAll(pageable);
    }

    @PostMapping("/add")
    public AddressDto addPost(@RequestBody AddressDto addressDto) {
        return addressService.save(addressDto);
    }

    @GetMapping("/get")
    public AddressDto update(@RequestParam String identifier) {
        return addressService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public AddressDto updatePost(@RequestBody AddressDto addressDto) {
        return addressService.update(addressDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String phoneNo) {
        try {
            addressService.delete(phoneNo);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/findByAllPhoneNo")
    public List<AddressDto> findAllByPhoneNo(@RequestParam String phoneNo) {
        return addressService.findAllByPhoneNo(phoneNo);
    }
}