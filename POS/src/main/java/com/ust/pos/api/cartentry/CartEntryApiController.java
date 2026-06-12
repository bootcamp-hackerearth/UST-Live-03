package com.ust.pos.api.cartentry;

import com.ust.pos.api.BaseController;
import com.ust.pos.cartentry.service.CartEntryService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartentry")
public class CartEntryApiController extends BaseController {

    @Autowired
    CartEntryService cartEntryService;

    @PostMapping("/add")
    public CartEntryDto addPost(@RequestBody CartEntryDto cartEntryDto) {
        return cartEntryService.save(cartEntryDto);
    }

    @PostMapping("/list")
    public WsDto<CartEntryDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartEntryService.findAll(pageable);
    }

    @GetMapping("/get")
    public CartEntryDto update(@RequestParam String identifier) {
        return cartEntryService.findByIdentifier(identifier);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartEntryService.deleteByIdentifier(identifier);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @GetMapping("/findcart")
    public List<CartEntryDto> findByCart(@RequestParam String cart) {
        return cartEntryService.findByCart(cart);
    }

}