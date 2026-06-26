package com.ust.pos.api.cart;

import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.CommonDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;
    private final CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/reduceEntry")
    public boolean reduceEntry(@RequestBody CartEntryDto cartEntryDto) {
        try {
            cartEntryService.reduceQuantity(cartEntryDto.getCartIdentifier(), cartEntryDto.getProductIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/getCart")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PostMapping("/addToCart")
    public CartDto addToCart(@RequestBody CartEntryDto cartEntryDto) {
        return cartService.recalculate(cartEntryDto.getCartIdentifier());
    }

    @PostMapping("/deleteCart")
    public Boolean deleteCart(@RequestBody CartDto cartDto) {
        try {
            cartService.deleteByIdentifier(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/deleteEntry")
    public boolean deleteEntry(@RequestBody CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getCartIdentifier() + "-" + cartEntryDto.getProductIdentifier();
        try {
            cartEntryService.delete(identifier);
            cartService.recalculate(cartEntryDto.getCartIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PostMapping("/clearcart")
    public void delete(@RequestBody CommonDto common) {
        cartEntryService.deleteAllByCartIdentifier(common.getIdentifier());
        cartService.recalculate(common.getIdentifier());
    }

}
