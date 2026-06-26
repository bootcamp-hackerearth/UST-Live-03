package com.ust.pos.api.cartentry;

import com.ust.pos.base.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cartentry")
public class CartEntryApiController extends BaseController {

    private final CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartentryDto) {
        return cartEntryService.save(cartentryDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartEntryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
