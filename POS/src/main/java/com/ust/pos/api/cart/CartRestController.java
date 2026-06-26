package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartRestController extends BaseController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/list")
    public List<CartDto> home(@RequestBody PaginationDto paginationDto) {
        Pageable pageable = getPageable(paginationDto.getPage(), paginationDto.getSizePerPage(), paginationDto.getSortDirection(), paginationDto.getSortField());
        return cartService.findAll(pageable);
    }

    @PostMapping("/add")
    public CartDto addPost(@RequestBody CartDto cartDto) {
        return cartService.save(cartDto);
    }

    @PostMapping("/get")
    public CartDto update(@RequestBody String identifier) {
        return cartService.findByIdentifier(identifier);
    }

    @PutMapping("/update")
    public CartDto updatePost(@RequestBody CartDto cartDto) {
        return cartService.update(cartDto);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody String identifier) {
        try {
            cartService.delete(identifier);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
