package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.CartDto;
import com.ust.pos.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartControllerApi extends BaseController {
    @Autowired
    private CartService cartService;

    @PostMapping("/list")
    public List<CartDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @GetMapping("/get")
    public CartDto update(@RequestParam String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PostMapping("/update")
    public CartDto updatePost(@RequestBody CartDto cartDto) {
        return cartService.update(cartDto);
    }

    @GetMapping("/delete")
    public boolean delete(@RequestParam String identifier) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
