package com.ust.pos.api.cartentry;


import com.ust.pos.cart.CartService;
import com.ust.pos.cartentry.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryApiController {
    private final CartEntryService cartEntryService;
    private final CartService cartService;

    public CartEntryApiController(CartEntryService cartEntryService, CartService cartService) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartEntryDto cartEntryDto){
        cartEntryService.save(cartEntryDto);
        return cartService.recalculateCart(cartEntryDto.getCartId());
    }
    @PostMapping("/getByCartId")
    public List<CartEntryDto> list(@RequestBody CartEntryDto cartEntryDto){
        return cartEntryService.findByCartId(cartEntryDto.getCartId());
    }
    @PostMapping("/update")
    public CartDto update(@RequestBody CartEntryDto cartEntryDto)
    {
        cartEntryService.update(cartEntryDto);

        return cartService.recalculateCart(cartEntryDto.getCartId());
    }
    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartEntryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/clearCart")
    public boolean clearCart(@RequestParam String cartId)
    {
        try {
            cartEntryService.clearCart(cartId);
            cartService.recalculateCart(cartId);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}