package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/cart")
@RestController
public class CartApiController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/addToCart")
    public CartDto addPost1(@RequestBody CartEntryDto cartEntryDto) {
        return cartService.recalculate(cartEntryDto.getCart());
    }

    @PostMapping("/getCart")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @GetMapping("/delete")
    public boolean delete(@RequestBody CartDto cartDto) {
        try {
            cartService.delete(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}