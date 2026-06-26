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

    public CartEntryApiController(
            CartEntryService cartEntryService,
            CartService cartService) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }


    @PostMapping("/add")
    public CartDto add(@RequestBody CartEntryDto cartEntryDto) {
        cartEntryService.save(cartEntryDto);
        return cartService.recalulateCart(cartEntryDto.getCartId());
    }

    @GetMapping("/getByCartId")
    public List<CartEntryDto> list(@RequestParam String cartId) {
        return cartEntryService.findByCartId(cartId);
    }

    @PutMapping("/updateQuantity")
    public CartDto updateQuantity(@RequestBody CartEntryDto dto) {

        cartEntryService.updateQuantity(dto);

        return cartService.recalulateCart(dto.getCartId());
    }

    @DeleteMapping("/delete")
    public CartDto delete(@RequestParam String identifier, @RequestParam String cartId) {
        cartEntryService.delete(identifier);
        return cartService.recalulateCart(cartId);
    }
}