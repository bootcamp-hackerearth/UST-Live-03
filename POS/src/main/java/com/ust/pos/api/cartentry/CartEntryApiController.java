package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryApiController extends BaseController {

    @Autowired
    private CartEntryService cartEntryService;

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
