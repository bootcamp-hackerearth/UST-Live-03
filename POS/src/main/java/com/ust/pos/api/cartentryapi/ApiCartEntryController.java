package com.ust.pos.api.cartentryapi;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartEntry")
public class ApiCartEntryController extends BaseController {

    public ApiCartEntryController(CartEntryService cartEntryService, CartService cartService) {
        this.cartEntryService = cartEntryService;
        this.cartService = cartService;
    }

    private final CartEntryService cartEntryService;
    private final CartService cartService;

    @PostMapping("/addEntry")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CASHIER','AUDITOR')")
    public CartEntryDto add(@RequestBody CartEntryDto cartEntryDto) {
        CartEntryDto cartEntryDto1 = cartEntryService.save(cartEntryDto);
        cartService.recalculate(cartEntryDto.getCart());
        return cartEntryDto1;
    }
}
