package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ApiCartController {

    private final CartService cartService;
    private final CartEntryService cartEntryService;

    public ApiCartController(CartService cartService, CartEntryService cartEntryService) {
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
    }

    @PostMapping("/add")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/list")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PostMapping("/addToCart")
    public CartDto addToCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartService.recalculate(cartEntryDto.getCart());
    }

    @DeleteMapping("/deleteCart")
    public Boolean deleteCart(@RequestBody CartDto cartDto) {
        try {
            cartService.deleteByIdentifier(cartDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DeleteMapping("/deleteEntry")
    public boolean deleteEntry(@RequestBody CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        try {
            cartEntryService.deleteByIdentifier(identifier);
            cartService.recalculate(cartEntryDto.getCart());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}