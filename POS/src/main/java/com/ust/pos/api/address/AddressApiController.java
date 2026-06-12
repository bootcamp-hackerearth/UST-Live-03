package com.ust.pos.api.address;

import com.ust.pos.adress.service.AddressService;
import com.ust.pos.api.BaseController;
import com.ust.pos.dto.AddressDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressApiController extends BaseController {
    @Autowired
    private AddressService addressService;

    @PostMapping("/add")
    public AddressDto add(@RequestBody AddressDto addressDto) {
        return addressService.save(addressDto);
    }

    @PostMapping("/list")
    public List<AddressDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage()
                , paginationDto.getSortDirection(), paginationDto.getSortField());
        return addressService.findAll(pageable);
    }

    @GetMapping("/get")
    public AddressDto getByIdentifier(@RequestParam String identifier) {
        return addressService.findByIdentifier(identifier);
    }

    @GetMapping("/getByPhoneNo")
    public List<AddressDto> getByPhoneNo(@RequestParam String phoneNo) {
        return addressService.findAllByPhoneNumber(phoneNo);
    }

    @PostMapping("/update")
    public AddressDto update(@RequestBody AddressDto addressDto) {
        return addressService.update(addressDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String phoneNo) {
        try {
            addressService.delete(phoneNo);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
