package com.ust.pos.api.cartentry;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryControllerApi {

    @Autowired
    private CartEntryService cartEntryService;

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartDto add(@RequestBody CartEntryDto cartEntryDto){
        cartEntryService.save(cartEntryDto);
        return cartService.recalculateCart(cartEntryDto.getCartId());
    }

    @PostMapping("/getByCartId")
    public List<CartEntryDto> list(@RequestBody CartEntryDto cartEntryDto){
        return cartEntryService.findByCartId(cartEntryDto.getCartId());
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartEntryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}