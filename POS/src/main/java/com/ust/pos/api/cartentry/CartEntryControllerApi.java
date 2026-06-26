package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartEntry")
public class CartEntryControllerApi extends BaseController {

    private final CartEntryService cartEntryService;

    public CartEntryControllerApi(CartEntryService cartEntryService) {
        this.cartEntryService=cartEntryService;
    }

    @PostMapping("/addEntry")
    public CartEntryDto add(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }
}
