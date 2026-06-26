package com.ust.pos.api.cart;

import com.ust.pos.api.BaseController;
import com.ust.pos.cart.service.CartService;
import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginationDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartApiController extends BaseController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/list")
    public WsDto<CartDto> home(@RequestBody PaginationDto paginationDto) {

        Pageable pageable = getPageable(paginationDto.getPage(),
                paginationDto.getSizePerPage(),
                paginationDto.getSortDirection(), paginationDto.getSortField());

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

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody CartDto cartDto) {

        String identifier = cartDto.getIdentifier();
        try {
            cartService.delete(identifier);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}


