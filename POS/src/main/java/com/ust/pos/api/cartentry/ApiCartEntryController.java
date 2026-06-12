package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;

import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")

public class ApiCartEntryController extends BaseController {

    @Autowired
    CartEntryService cartEntryService;

    @PostMapping("/addEntry")
    public CartEntryDto addCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @PostMapping("/findActiveStatus")
    public List<CartEntryDto> findActive() {
        return cartEntryService.findActiveStatus();
    }
}
