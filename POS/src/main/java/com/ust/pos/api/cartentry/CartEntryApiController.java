package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryApiController extends BaseController {

    private final CartEntryService cartEntryService;

    public CartEntryApiController(CartEntryService cartEntryService) {
        this.cartEntryService = cartEntryService;
    }

    @PostMapping("/list")
    public List<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

        return cartEntryService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartEntryDto) {

        return cartEntryService.save(cartEntryDto);
    }

    @PutMapping("/updatequantity")
    public CartEntryDto updatePost(@RequestBody CartEntryDto cartEntryDto) {

        return cartEntryService.updateQuantity(cartEntryDto);
    }

    @PostMapping("/get")
    public CartEntryDto update(@RequestBody String identifier) {

        return cartEntryService.findByIdentifier(identifier);
    }

    @PostMapping("/cart")
    public List<CartEntryDto> findByCartId(@RequestBody String cartId) {

        return cartEntryService.findByCartId(cartId);
    }

    @DeleteMapping("/clearCart")
    public boolean deleteAll(@RequestBody String cartId) {

        try {
            cartEntryService.deleteAllByCartId(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {

        try {
            cartEntryService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}


