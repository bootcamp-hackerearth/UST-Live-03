package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/cart")
@RestController
public class CartApiController extends BaseController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

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

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody CartDto cartDto) {
        try {
            cartService.delete(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}