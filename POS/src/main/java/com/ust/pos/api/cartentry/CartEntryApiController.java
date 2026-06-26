package com.ust.pos.api.cartentry;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
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
    public CartDto add(@RequestBody CartEntryDto cartEntryDto) {
        cartEntryService.save(cartEntryDto);
        return cartService.recalulateCart(cartEntryDto.getCartId());
    }

    @PostMapping("/getByCartId")
    public List<CartEntryDto> list(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.findByCartId(cartEntryDto.getCartId());
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
    @GetMapping("/list")
    public List<CartEntryDto> list() {
        return cartEntryService.findAll();
    }

    @PutMapping("/update")
    public CartEntryDto update(
            @RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.update(cartEntryDto);
    }
    @PutMapping("/decrease")
    public CartEntryDto decrease(@RequestParam String identifier) {
        return cartEntryService
                .decreaseQuantity(identifier);
    }
    @PostMapping("/clear")
    public boolean clearCart(
            @RequestParam String cartId) {
        try {

            List<CartEntryDto> entries =
                    cartEntryService.findByCartId(cartId);

            for (CartEntryDto entry : entries) {

                cartEntryService.delete(
                        entry.getIdentifier()
                );
            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
}