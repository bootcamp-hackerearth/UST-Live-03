package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('Admin','Cashier')")
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

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('Admin','Cashier')")
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
    @PreAuthorize("hasAnyAuthority('Admin','Cashier')")
    public boolean deleteAll() {
        try {
            cartService.deleteAll();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}