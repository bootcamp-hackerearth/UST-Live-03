package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController extends BaseController {
    private final CartService cartService;
    private final CartEntryService cartEntryService;

    public CartApiController(CartService cartService, CartEntryService cartEntryService) {
        this.cartService = cartService;
        this.cartEntryService = cartEntryService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','SUPPORT')")
    public CartDto addCart(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/getCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public CartDto getCart(@RequestBody CartDto cartDto) {
        return cartService.findByIdentifier(cartDto.getIdentifier());
    }

    @PutMapping("/addToCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public CartDto addToCart(@RequestBody CartDto cartDto) {
        return cartService.recalculate(cartDto.getIdentifier());
    }

    @DeleteMapping("/deleteCart")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public Boolean deleteCart(@RequestBody CartDto cartDto) {
        try {
            cartService.deleteByIdentifier(cartDto.getIdentifier());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/deleteEntry")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER')")
    public boolean deleteEntry(@RequestBody CartEntryDto cartEntryDto) {
        String identifier = cartEntryDto.getProduct() + "-" + cartEntryDto.getCart();
        try {
            cartEntryService.deleteByIdentifier(identifier);
            cartService.recalculate(cartEntryDto.getCart());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
