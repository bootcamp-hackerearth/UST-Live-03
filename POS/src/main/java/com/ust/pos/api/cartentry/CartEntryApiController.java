package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartEntry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartEntry")
public class CartEntryApiController extends BaseController {

    @Autowired
    private CartEntryService cartEntryService;

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

    @GetMapping("/get")
    public CartEntryDto update(@RequestParam String identifier) {

        return cartEntryService.findByIdentifier(identifier);
    }

    @GetMapping("/clearCart")
    public boolean deleteAll(@RequestParam String cartId) {
        try {
            cartEntryService.deleteAllByCartId(cartId);
            return true;
        } catch (Exception e) {
            return false;
        }
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


