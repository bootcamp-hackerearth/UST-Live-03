package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
public class ApiCartEntryController {

    private final CartEntryService cartEntryService;
    private final CartService cartService;

    public ApiCartEntryController(CartEntryService cartEntryService, CartService cartService) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }

    @PostMapping("/addEntry")
    public CartEntryDto add(@RequestBody CartEntryDto cartEntryDto) {
        CartEntryDto saved = cartEntryService.save(cartEntryDto);
        cartService.recalculate(cartEntryDto.getCart());
        return saved;
    }

    @PostMapping("/getAllCartEntry")
    public List<CartEntryDto> getCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.findAllEntriesForCart(cartEntryDto.getCart());
    }

    @PutMapping("/updateEntry")
    public CartEntryDto updateCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.update(cartEntryDto);
    }
}