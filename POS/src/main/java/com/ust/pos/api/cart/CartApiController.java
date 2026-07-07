package com.ust.pos.api.cart;


import com.ust.pos.cart.CartService;
import com.ust.pos.dto.CartDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasAnyAuthority('Admin', 'Cashier')")
public class CartApiController {
    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto){
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
    @GetMapping("/get")
    public CartDto get(@RequestParam String identifier)
    {
        return cartService.findByIdentifier(identifier);
    }
    @GetMapping("/recalculate")
    public CartDto recalculate(@RequestParam String cartId)
    {
        return cartService.recalculateCart(cartId);
    }
    @PostMapping("/update")
    public CartDto update(@RequestBody CartDto cartDto)
    {
        return cartService.update(cartDto);
    }
}
