package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            cartService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
