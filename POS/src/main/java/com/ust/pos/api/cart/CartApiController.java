package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @GetMapping("/get")
    public CartDto get(
            @RequestParam String identifier
    ) {
        return cartService.findByIdentifier(identifier);
    }

    @GetMapping("/list")
    public List<CartDto> list() {
        return cartService.findAll();
    }

    @GetMapping("/delete")
    public boolean delete(
            @RequestParam String identifier
    ) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/deleteAll")
    public boolean deleteAll() {
        try {
            cartService.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}