package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartentryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cartEntry")
public class CartentryRestController extends BaseController {

    private final CartentryService cartEntryService;

    public CartentryRestController(CartentryService cartEntryService) {
        this.cartEntryService = cartEntryService;
    }

    @PostMapping("/list")
    public WsDto<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartEntryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @GetMapping("/get")
    public CartEntryDto update(@RequestParam String identifier) {
        return cartEntryService.findByIdentifier(identifier);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String identifier, String cartId) {
        try {
            cartEntryService.delete(identifier, cartId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @DeleteMapping("/clearCart")
    public boolean deleteAll(@RequestParam String cartId) {
        try {
            cartEntryService.deleteAllByCartId(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PutMapping("/updateQuantity")
    public CartEntryDto update(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.updateQuantity(cartEntryDto);
    }

}
