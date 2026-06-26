package com.ust.pos.api.cartentry;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryControllerApi {

    private final CartEntryService cartEntryService;
    private final CartService cartService;

    public CartEntryControllerApi(
            CartEntryService cartEntryService,
            CartService cartService
    ) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartEntryDto cartEntryDto) {
        cartEntryService.save(cartEntryDto);
        return cartService.recalculateCart(cartEntryDto.getCartId());
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

    @GetMapping("/clear")
    public boolean clearCart(@RequestParam String cartId) {
        try {
            cartEntryService.deleteByCartId(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}