package com.ust.pos.api.cart;


import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
public class ApiCartEntryController {

    @Autowired
    private CartEntryService cartEntryService;

    @PostMapping("/addEntry")
    public CartEntryDto add(@RequestBody CartEntryDto cartEntryDto) {

        return cartEntryService.save(cartEntryDto);
    }

    @PostMapping("/getAllCartEntry")
    public List<CartEntryDto> getCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.findAllEntriesForCart(cartEntryDto.getCart());
    }

    @PostMapping("/updateEntry")
    public CartEntryDto updateCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.update(cartEntryDto);
    }


}
