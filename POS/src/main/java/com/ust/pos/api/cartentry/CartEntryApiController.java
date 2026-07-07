package com.ust.pos.api.cartentry;


import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryApiController {
    private final CartEntryService cartEntryService;
    private final CartService cartService;

    public CartEntryApiController(CartService cartService, CartEntryService cartEntryService) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('Admin','Cashier')")
    public CartDto add(@RequestBody CartEntryDto cartEntryDto) {
        cartEntryService.save(cartEntryDto);
        return cartService.recalculateCart(cartEntryDto.getCartId());
    }

    @PostMapping("/getByCartId")
    @PreAuthorize("hasAnyAuthority('Admin','Cashier')")
    public List<CartEntryDto> list(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.findByCartId(cartEntryDto.getCartId());
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin','Cashier')")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartEntryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PutMapping("/update")
    public CartDto update(
            @RequestBody CartEntryDto cartEntryDto) {

        cartEntryService.update(cartEntryDto);

        return cartService.recalculateCart(
                cartEntryDto.getCartId());
    }

}