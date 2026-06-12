package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cartentry")

public class CartEntryControllerApi extends BaseController {

    @Autowired
    CartEntryService cartEntryService;

    @PostMapping("/addEntry")
    public CartEntryDto addCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }
}
