package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @GetMapping("/list")
    public List<CartDto> list() {
        return cartService.findAll();
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}