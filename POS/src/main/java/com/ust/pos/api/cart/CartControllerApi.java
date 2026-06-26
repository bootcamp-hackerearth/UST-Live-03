package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartControllerApi {

    private final CartService cartService;

    public CartControllerApi(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            cartService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}