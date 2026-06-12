package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")

public class CartControllerApi extends BaseController {
    @Autowired
    CartService cartService;
    @Autowired
    CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/getCart")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PostMapping("/addToCart")
    public CartDto addtocart(@RequestBody CartEntryDto cartEntryDto) {
        return cartService.recalculate(cartEntryDto.getCart());
    }

    @GetMapping("/deleteCart")
    public boolean deleteCart(@RequestBody CartDto cartDto) {
        try {
            cartService.deleteByIdentifier(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/deleteEntry")
    public boolean deleteEntry(@RequestBody CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        try {
            cartEntryService.deleteByIdentifier(identifier);
            cartService.recalculate(cartEntryDto.getCart());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
