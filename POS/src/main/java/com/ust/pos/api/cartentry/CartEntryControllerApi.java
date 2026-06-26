package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.cartEntry.service.CartEntryService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
public class CartEntryControllerApi extends BaseController {

    private final CartEntryService cartEntryService;

    public CartEntryControllerApi(CartEntryService cartEntryService) {
        this.cartEntryService = cartEntryService;
    }

    @PostMapping("/list")
    public List<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
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
    public boolean delete(@RequestParam String cartId, @RequestParam String product) {
        try {
            cartEntryService.delete(cartId,product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/clearCart")
    public boolean deleteAll(@RequestParam String cartId){
        try {
            cartEntryService.deleteAllByCartId(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping
    public List<CartEntryDto> findByCartId(@RequestParam String cartId){
        return cartEntryService.findByCartId(cartId);
    }

    @PostMapping("/updateQuantity")
    public CartEntryDto updateQuantity(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.updateQuantity(cartEntryDto);
    }
}
