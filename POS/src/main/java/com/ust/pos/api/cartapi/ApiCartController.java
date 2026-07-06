package com.ust.pos.api.cartapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.cart.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','AUDITOR')")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/getCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','AUDITOR')")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PostMapping("/addToCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','AUDITOR')")
    public CartDto addToCart(@RequestBody CartDto cartDto){
        return cartService.recalculate(cartDto.getIdentifier());
    }

    @PutMapping("/deleteCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','AUDITOR')")
    public Boolean deleteCart(@RequestBody CartDto cartDto){
        try {
            cartService.deleteByIdentifier(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @PutMapping("/deleteEntry")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','AUDITOR')")
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