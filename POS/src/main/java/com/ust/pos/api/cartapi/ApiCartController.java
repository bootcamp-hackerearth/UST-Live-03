package com.ust.pos.api.cartapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.cart.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class ApiCartController extends BaseController {

    public ApiCartController(CartService cartService, CartEntryService cartEntryService) {
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
    }

    private final CartService cartService;
    private final CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/getCart")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PostMapping("/addToCart")
    public CartDto addToCart(@RequestBody CartDto cartDto){
        return cartService.recalculate(cartDto.getIdentifier());
    }

    @PutMapping("/deleteCart")
    public Boolean deleteCart(@RequestBody CartDto cartDto){
        try {
            cartService.deleteByIdentifier(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PutMapping("/deleteEntry")
    public boolean delete(@RequestParam String identifier, String cart) {
        try{
            cartEntryService.deleteByIdentifier(identifier);
            cartService.recalculate(cart);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
}