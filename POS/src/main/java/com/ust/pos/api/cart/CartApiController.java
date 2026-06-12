package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/getCart")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PostMapping("/addToCart")
    public CartDto addToCart(@RequestBody CartDto cartDto) {
        return cartService.recalculate(cartDto.getIdentifier());
    }

    @GetMapping("/deleteEntry")
    public boolean delete(@RequestParam String identifier, String cart) {
        try {
            cartEntryService.delete(identifier);
            cartService.recalculate(cart);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}