package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.ui.Model;
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

    @GetMapping("/delete")
    public boolean delete(Model model, @RequestParam String identifier) {
        try {
            cartService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    @GetMapping("/list")
    public List<CartDto> list() {
        return cartService.findAll();
    }

    @PutMapping("/update")
    public CartDto update(
            @RequestBody CartDto cartDto) {
        return cartService.update(cartDto);
    }
    @PutMapping("/updateCustomer")
    public CartDto updateCustomer(
            @RequestParam String cartId,
            @RequestParam String customerId) {
        return cartService.updateCustomer(
                cartId,
                customerId
        );
    }
}
