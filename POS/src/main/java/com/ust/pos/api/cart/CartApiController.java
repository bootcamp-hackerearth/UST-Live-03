package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartEntryService cartEntryService;
    private final CartService cartService;

    public CartApiController(
            CartEntryService cartEntryService,
            CartService cartService) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PutMapping("/update")
    public CartDto update(@RequestBody CartDto cartDto) {
        return cartService.update(cartDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DeleteMapping("/deleteAll")
    public boolean deleteAll(@RequestParam String cartId) {
        try {
            cartService.delete(cartId);
            cartEntryService.deleteAll(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/applyCoupon")
    public CartDto applyCoupon(
            @RequestParam String cartId,
            @RequestParam String couponCode
    ) {
        return cartService.applyCoupon(cartId, couponCode);
    }

    @DeleteMapping("/removeCoupon")
    public CartDto removeCoupon(
            @RequestParam String cartId
    ) {
        return cartService.removeCoupon(cartId);
    }

    @GetMapping("/getById")
    public CartDto getById(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }
}