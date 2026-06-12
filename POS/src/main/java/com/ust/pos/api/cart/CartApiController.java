package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.CartDto;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartApiController extends BaseController {

    @Autowired
    private CartService cartService;

    @PostMapping("/list")
    public List<CartDto> list(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartDto add(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @GetMapping("/get")
    public CartDto get(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CartDto update(@RequestBody CartDto cartDto) {
        return cartService.update(cartDto);
    }

    @PostMapping("/delete")
    public boolean delete(@RequestBody CartDto cartDto) {
        try {
            cartService.delete(cartDto.getIdentifier());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PostMapping("/recalculate")
    public CartDto reCalculate(@RequestBody CartEntryDto cartEntryDto) {
        return cartService.reCalculate(cartEntryDto.getCartIdentifier());
    }
}